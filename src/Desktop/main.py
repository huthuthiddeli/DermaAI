import customtkinter as ctk

class LoginRegisterApp(ctk.CTk):
    def __init__(self):
        super().__init__()

        # Window settings
        self.title("Login / Register Page")
        self.geometry("400x400")
        ctk.set_appearance_mode("dark")
        ctk.set_default_color_theme("blue")

        # Define mode: "Login" or "Register"
        self.mode = "Login"

        # Header Label
        self.header_label = ctk.CTkLabel(self, text="Login", font=("Arial", 20, "bold"))
        self.header_label.pack(pady=10)

        # Email Field
        self.email_label = ctk.CTkLabel(self, text="Email:")
        self.email_label.pack()
        self.email_entry = ctk.CTkEntry(self, placeholder_text="Enter your email", width=250)
        self.email_entry.pack(pady=5)

        # Password Field
        self.password_label = ctk.CTkLabel(self, text="Password:")
        self.password_label.pack()
        self.password_entry = ctk.CTkEntry(self, placeholder_text="Enter your password", show="*", width=250)
        self.password_entry.pack(pady=5)

        # Confirm Password Field (Initially Hidden)
        self.confirm_password_label = ctk.CTkLabel(self, text="Confirm Password:")
        self.confirm_password_entry = ctk.CTkEntry(self, placeholder_text="Confirm your password", show="*", width=250)
        
        # Submit Button
        self.submit_button = ctk.CTkButton(self, text="Login", command=self.submit_action)
        self.submit_button.pack(pady=10)

        # Switch to Register/Login
        self.switch_label = ctk.CTkLabel(self, text="Don't have an account?")
        self.switch_label.pack()
        self.switch_button = ctk.CTkButton(self, text="Switch to Register", command=self.switch_mode, fg_color="gray")
        self.switch_button.pack(pady=5)

    def switch_mode(self):
        if self.mode == "Login":
            self.mode = "Register"
            self.header_label.configure(text="Register")
            self.submit_button.configure(text="Register")
            self.switch_button.configure(text="Switch to Login")
            self.switch_label.configure(text="Already have an account?")

            self.confirm_password_label.pack(after=self.password_entry, pady=(5, 0))
            self.confirm_password_entry.pack(after=self.confirm_password_label, pady=(5, 5))

        else:
            self.mode = "Login"
            self.header_label.configure(text="Login")
            self.submit_button.configure(text="Login")
            self.switch_button.configure(text="Switch to Register")
            self.switch_label.configure(text="Don't have an account?")

            self.confirm_password_label.pack_forget()
            self.confirm_password_entry.pack_forget()

    def submit_action(self):
        email = self.email_entry.get()
        password = self.password_entry.get()

        if self.mode == "Login":
            print(f"Logging in with Email: {email}, Password: {password}")
            # login
        elif self.mode == "Register":
            confirm_password = self.confirm_password_entry.get()
            if password == confirm_password:
                print(f"Registering with Email: {email}, Password: {password}")
                # register
            else:
                print("Error: Passwords do not match!")


if __name__ == "__main__":
    app = LoginRegisterApp()
    app.mainloop()

