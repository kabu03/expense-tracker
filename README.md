# Expense Tracker (Java)
Welcome to Expense Tracker, a simple and efficient tool for managing your personal or business expenses. This Java-based application provides a user-friendly interface for tracking and organizing your expenses.

<img width="960" alt="ExpenseTrackerGUI" src="https://github.com/kabu03/expense-tracker/assets/118374503/2bcc529e-c70e-4f83-9473-4cb1152bdfa6">

## Features

- **Add, Edit, and Remove Expenses**: Easily manage your expenses by adding new entries, editing existing ones, or removing those you no longer need.
- **Filter Expenses by Month**: View your expenses for any specific month to keep track of your monthly spending.
- **View Total Spending**: See your total spending per month, for all time, and broken down by category (e.g., food, entertainment, groceries).
- **Real-Time Currency Conversion**: Convert expenses in multiple currencies with support for over 30 currencies, ensuring accurate financial tracking.

## Setup and Installation

To get started with Expense Tracker:

### 1) Clone the Repository

Use the following command to clone the repository:
```git clone https://github.com/kabu03/expense-tracker.git```

### 2) Open and Run the Project
Open the project in your preferred Java IDE.
Run the application from the main class.

### 3) Rename the Expense File
Before using the expense tracker, rename the expenses_sample.ser file to expenses.ser in the project's root directory. This file will be used to store your expense data using Java serialization.

#### Custom File Name (Optional)
If you prefer a different file name, create the file with your chosen name. Then, open ExpenseFileHandler.java, and change the file name in the code to match your new file name.

## Getting an API Key
This application uses the ExchangeRate-API for currency conversion. You need to get an API key from ExchangeRate-API, which has a free tier.

### Setting Up Your API Key
Once you have your API key, you need to set it up in your environment using Environment Variables.
Set the environment variable EXCHANGE_RATE_API_KEY to your API key.

On Windows:
`$env:EXCHANGE_RATE_API_KEY="your_api_key_here"`

On macOS/Linux:
`export EXCHANGE_RATE_API_KEY="your_api_key_here"`

## Contributing

Your contributions are welcome! If you have suggestions or improvements, feel free to fork the repo and submit a pull
request.

## Questions or Issues

If you have any questions or encounter any issues, please open an issue in the GitHub repository.
