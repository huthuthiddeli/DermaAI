export const hashPassword = async(hashedPassword: string) => {
    const password = new TextEncoder().encode(hashedPassword); // GET STRING FROM UTF-8
    const hashBuffer = await crypto.subtle.digest("SHA-256", password);
    const hashArray = Array.from(new Uint8Array(hashBuffer));
    const hashHex = hashArray
    .map((b) => b.toString(16).padStart(2, "0"))
    .join(""); // convert bytes to hex string
  return hashHex;
}