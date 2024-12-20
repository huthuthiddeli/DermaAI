package main

import (
	"fmt"
	"net/http"
)

func handler(w http.ResponseWriter, r *http.Request) {
	// Correct declaration of jsonString
	// var jsonString = `{
	// 	"id": 1,
	// 	"name": "John Doe",
	// 	"email": "johndoe@example.com",
	// 	"age": 30,
	// 	"isActive": true
	// }`

	// Using the JSON string in the response
	fmt.Fprintln(w, "Hello, World!")
	//fmt.Fprintln(w, jsonString)
}

func main() {
	http.HandleFunc("/", handler) // Route to the root endpoint
	fmt.Println("Server is running on http://localhost:8080")
	err := http.ListenAndServe(":8080", nil) // Start the server on port 8080
	if err != nil {
		fmt.Println("Error starting server:", err)
	}
}
