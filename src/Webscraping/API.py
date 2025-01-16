from fastapi import FastAPI, HTTPException, Request
from pydantic import BaseModel
from typing import List
from datetime import date, datetime
import uvicorn
import json

app = FastAPI()

# Objekt-Liste
students = []

# Datenpfad fÃ¼r die JSON-Datei
DATA_FILE = "data/datensatz.json"


class Student(BaseModel):
    id: int
    name: str
    birth_date: date
    entry_date: date
    class_name: str

    # Hilft beim Serialiseren der Daten.
    # Validaation von Datentypen auch gegeben, da Python das sonst nicht versteht :)
    def to_dict(self):
        return {
            'id': self.id,
            'name': self.name,
            'birth_date': self.birth_date.isoformat() if isinstance(self.birth_date, date) else self.birth_date,
            'entry_date': self.entry_date.isoformat() if isinstance(self.entry_date, date) else self.entry_date,
            'class_name': self.class_name
        }

    # Hilft bei der deserializierung und gibt einen formatieren string zurÃ¼ck
    def __repr__(self):
        return f"MyClass(id={self.id}, name='{self.name}', birth_date={self.birth_date}, entry_date={self.entry_date}, class_name='{self.class_name}')"

# Middleware: Vor jedem Request aufgerufen, speichert nach dem Aufruf die Daten in die Datei
# per aufruf der save_data Funktion
@app.middleware("http")
async def readBufferChanges(request: Request, call_next):
    req = await call_next(request)
    save_data()
    return req

def read_from_file():
    # Ã–ffnet Datei in reading-mode und speichert gelesene Daten in die Objekt-Liste
    try:
        with open(DATA_FILE, 'r') as file:
            data = json.load(file)
    except ValueError:
        # KÃ¶nnte versuchen den Logger von Uvicorn fÃ¼r diese Meldung zu benutzen
        # jedoch reicht diese Meldung auch ohne Format, da die Applikation ja sowieso
        # funktioniert.
        print(f"File was empty!\n")
        return

    # Serialisiert Objekte, JSON-Serializer irgendwie zu komisch.
    for item in data:
        obj = Student(
            id=item['id'],
            name=item['name'],
            birth_date=datetime.strptime(item['birth_date'], '%Y-%m-%d').date(), entry_date=datetime.strptime(item['entry_date'], '%Y-%m-%d').date(),
            class_name=item['class_name']
        )
        students.append(obj)

# Funktion: speichert neue Daten in die Datein, aufgerufen durch Middleware
def save_data():
    with open(DATA_FILE, "w") as file:
        json.dump([student.to_dict() for student in students], file, indent=4)

# Endpunkt: Neuen SchÃ¼ler erstellen
@app.post("/students/", response_model=Student)
def create_student(student: Student):
    student.id = max((s.id for s in students), default=0) + 1
    students.append(student)
    save_data()
    return student

# Endpunkt: Alle SchÃ¼ler auflisten
@app.get("/students/", response_model=List[Student])
def list_students():
    return students

# Endpunkt: SchÃ¼ler mit bestimmter ID suchen
@app.get("/students/{student_id}", response_model=Student)
def get_student(student_id: int):
    for student in students:
        if student.id == student_id:
            return student
    raise HTTPException(status_code=404, detail="Student not found")

# Endpunkt: SchÃ¼lerdaten aktualisieren
@app.put("/students/{student_id}", response_model=Student)
def update_student(student_id: int, updated_student: Student):
    for i, student in enumerate(students):
        if student.id == student_id:
            updated_student.id = student_id
            students[i] = updated_student
            save_data()
            return updated_student
    raise HTTPException(status_code=404, detail="Student not found")

# Endpunkt: SchÃ¼ler lÃ¶schen
@app.delete("/students/{student_id}")
def delete_student(student_id: int):
    global students
    for i, student in enumerate(students):
        if student.id == student_id:
            del students[i]
            save_data()
            return {"message": "Student deleted successfully"}
    raise HTTPException(status_code=404, detail="Student not found")

# Endpunkt: Beispieldatensatz generieren
@app.get("/initialize/")
def initialize_data():
    global students
    students = [
        Student(id=1, name="jonas maier", birth_date="2005-05-15", entry_date="2020-09-01", class_name="5AHINF"),
        Student(id=2, name="Andi Arbeit", birth_date="2006-07-20", entry_date="2021-09-01", class_name="?"),
        Student(id=3, name="Ralph", birth_date="2005-11-12", entry_date="2020-09-01", class_name="?")
    ]
    return {"message": "Sample data initialized"}

if __name__ == "__main__":
    read_from_file()
    uvicorn.run(app, host="0.0.0.0", port=8000)