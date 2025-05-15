# Use an official Python base image
FROM python:3.11-slim

# Prevent prompts from interactive installers
ENV DEBIAN_FRONTEND=noninteractive

# Install system dependencies
RUN apt-get -o Acquire::Check-Valid-Until=false update && \
    apt-get install -y --no-install-recommends \
    apt-utils \
    ca-certificates \
    wget \
    curl \
    gnupg \
    libgl1 \
    libglib2.0-0 \
    && rm -rf /var/lib/apt/lists/*


# Set environment variables
ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1

# Set working directory
WORKDIR /app

# Copy requirements and install
COPY requirements.txt .
RUN pip install --upgrade pip && pip install -r requirements.txt

# Copy your application code
COPY ./src ./src

# Copy the HAM10000_metadata.csv file
COPY HAM10000_metadata.csv ./src/

# Set working directory to where your API.py is
WORKDIR /app/src

# Start the FastAPI server
CMD ["uvicorn", "API:app", "--host", "0.0.0.0", "--port", "6969"]