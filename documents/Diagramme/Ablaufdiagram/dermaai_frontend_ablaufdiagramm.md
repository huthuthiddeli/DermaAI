```mermaid
flowchart TD
    A[Start] --> B{User logged in?}
    B -- No --> C[Register/Login] --> D[Show past predictions]
    B -- Yes --> D[Fetch past predictions]
    
    D --> E[User selects ai model]
    E --> F[Take a photo]
    
    F --> G{Should AI crop the image?}
    G -- Yes --> H[Send image to backend]
    H --> I[Receive cropped image]
    I --> J{Use it?}
    J -- No --> G
    J -- Yes --> K[Send image to AI backend]
    
    G -- No --> L[User crops image manually] --> K[Send image to AI backend]
    
    K --> M[Receive prediction]
    M --> N{Save to database?}
    N -- Yes --> O[Store in DB]
    N -- No --> P[Continue without saving]
    
    O --> Q[End]
    P --> Q[End]
