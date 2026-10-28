/**
 * Smart Expense Tracker - Client-side Helpers & Validations
 * Advanced Internet Programming Mini Project
 */

document.addEventListener("DOMContentLoaded", function () {
    // 1. Password confirmation check on registration form
    const regForm = document.getElementById("registerForm");
    if (regForm) {
        regForm.addEventListener("submit", function (e) {
            const pass = document.getElementById("password").value;
            const confirmPass = document.getElementById("confirmPassword").value;
            if (pass !== confirmPass) {
                e.preventDefault();
                alert("Passwords do not match. Please re-enter.");
                document.getElementById("confirmPassword").focus();
            }
        });
    }

    // 2. Positive number validation on expense and budget forms
    const amountInputs = document.querySelectorAll("input[type='number'][name='amount']");
    amountInputs.forEach(input => {
        input.addEventListener("input", function () {
            if (parseFloat(this.value) <= 0) {
                this.setCustomValidity("Amount must be greater than zero.");
            } else {
                this.setCustomValidity("");
            }
        });
    });

    // 3. Auto-submit month selector filters
    const monthSelectors = document.querySelectorAll(".auto-submit-month");
    monthSelectors.forEach(select => {
        select.addEventListener("change", function () {
            this.form.submit();
        });
    });
});

/**
 * Global helper for confirming deletions
 */
function confirmAction(message) {
    return confirm(message || "Are you sure you want to proceed with this deletion?");
}

