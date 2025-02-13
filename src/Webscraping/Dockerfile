FROM python:3.12

# Set the working directory
WORKDIR /app

# Install system dependencies
RUN apt-get update && apt-get install -y \
    libgl1-mesa-glx \
    libglib2.0-0

# Copy requirements.txt and install Python dependencies
COPY requirements.txt /app/requirements.txt
RUN pip install --no-cache-dir -r requirements.txt

# Copy the application code
COPY API.py /app/API.py
COPY find_spot.py /app/find_spot.py

# Expose the port
EXPOSE 6969

# Command to run the application
CMD ["uvicorn", "API:app", "--host", "0.0.0.0", "--port", "6969"]