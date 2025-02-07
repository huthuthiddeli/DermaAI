FROM python:3.12

# Set the working directory
WORKDIR /app

COPY requirements.txt /app/requirements.txt

# Install dependencies
RUN pip install -r requirements.txt

# Copy the API.py and find_spot.py files
COPY API.py /app/API.py
COPY find_spot.py /app/find_spot.py

# Expose the port
EXPOSE 6969

# Command to run the application
CMD ["python", "API.py"]