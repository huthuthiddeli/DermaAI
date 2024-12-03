class Picture:

    def __init__(self, pic, diagnosis):
        self.picture = pic
        self.diagnosis = diagnosis

    def to_dict(self) -> dict:
        return {
            "picture": self.picture,
            "diagnosis": self.diagnosis
        }

    def set_picture(self, pic):
        self.picture = pic
        return self

    def get_picture(self):
        return self.picture

    def get_diagnosis(self) -> str:
        return self.diagnosis

    def to_string(self) -> str:
        return "Diagnosis:\t" + self.diagnosis + "\nPicture:\t" + self.picture + "\n"
