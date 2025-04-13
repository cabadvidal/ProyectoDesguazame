// User registration
function registerUser(email, password) {
    auth.createUserWithEmailAndPassword(email, password)
      .then((userCredential) => {
        // Signed in 
        const user = userCredential.user;
        console.log("User registered successfully:", user.uid);
        //Further actions after successful registration (redirect, etc)
      })
      .catch((error) => {
        const errorCode = error.code;
        const errorMessage = error.message;
        console.error("Error registering user:", errorCode, errorMessage);
        //Handle errors appropriately, such as displaying error messages to the user.
      });
  }

