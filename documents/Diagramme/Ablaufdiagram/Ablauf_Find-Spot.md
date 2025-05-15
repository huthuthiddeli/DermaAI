````mermaid
graph LR;
    A(Start) --> B[Decode Image from Bytes];
    B --> C[Get Image Dimensions];
    C --> D[Initialize Variables];
    D --> E[Loop Until Max Iterations];
    E -->|Extract ROI| F[Crop ROI Based on Iteration];
    F -->|Process ROI| G[Convert to Grayscale & Detect Edges];
    G -->|Find Contours| H[Detect Regions];
    H -->|Filter Contours| I{Valid Contour Found?};
    I --No--> J[Increase ROI Size & Continue];
    I --Yes--> K[Extract Largest Detected Spot];
    K -->|Compare With Previous Spot| L{Is Largest So Far?};
    L --Yes--> M[Update Largest Spot];
    L --No--> J;
    M -->|Check Termination Condition| N{Same Size as Last?};
    N --Yes--> O[Display & Return Spot];
    N --No--> J;
    J -->|Increment Iteration| E;
    O --> P[End];
```