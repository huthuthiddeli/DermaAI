```mermaid
graph LR;
    A(Start) -->|Call fetch_from_isic_archive| B[Fetch HTML from URL];
    B -->|Convert HTML to JSON| C[Parse JSON Data];
    C -->|Extract 'results' and 'next'| D[Process Data];
    D -->|Loop Through Results| E[Extract Diagnosis & Picture];
    E -->|Check if Data is Valid| F{Valid Data};
    F --No--> D[Skip Iteration];
    F --Yes--> G[Print Data & Download Image];
    G -->|Run DownloadImage.py| H[Execute Subprocess];
    H -->|Check Output/Error| I{Output Available};
    I --Yes--> J[Print Logs];
    I --No--> D[Continue];
    D -->|Check for Next Link| K{Next Link Available};
    K --Yes--> B[Recursive Call to fetch_from_isic_archive];
    K --No--> L[End];

    subgraph Subprocess Execution
        H1[Check for Arguments] -->|Assert sys.argv > 1| H2{Valid Arguments?};
        H2 --No--> H3[Exit with Error];
        H2 --Yes--> H4[Parse JSON Data];
        H4 -->|Download Image| H5[Save Image Locally];
        H5 -->|Send Image to API| H6[Receive Processed Image];
        H6 -->|Check Response| H7{Valid Response?};
        H7 --No--> H8[Exit with Error];
        H7 --Yes--> H9[Convert Response to NumPy Array];
        H9 -->|Prepare JSON Payload| H10[Send Processed Data];
        H10 -->|POST to API| H11[Check API Response];
    end

```