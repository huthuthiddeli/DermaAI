# DermaAI
DermaAI is the diploma project of the three students Jonas Maier, Daniel Jessner and Jonas Bogensberger for their graduation from the HTL Saalfelden for computer science in 2025. It involves the development of a mobile application that allows the user to view images of skin lesions using certain AI to have models evaluated. The AI ​​models are trained using self-collected medical data and then evaluated.

## Structure
```mermaid
flowchart TD
    %% Mobile App Section
    MobileApp[Kotlin Mobile App]
    MobileApp -->|Send Images| API

    %% API Section
    subgraph KI_API[KI API in Python]
        API[API Endpoint]
        API --> Model[KI Model]
        Model --> Evaluate[Evaluate Image Data]
    end

    %% Database Section
    subgraph MongoDB[Datenbank Backend]
        TrainingData[(Provide Training Data)]
        UserData[(Store User Data)]
    end

    %% Connections
    API -->|Fetch Training Data| TrainingData
    API -->|Return Results| MobileApp
    MobileApp -->|Login/Logout| UserData
    UserData --> |Provide Userata|MobileApp

