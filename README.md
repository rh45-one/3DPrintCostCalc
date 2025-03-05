# 3D Printer-Cost-Calculator

## Overview
This Java CLI-based application helps estimate 3D printing costs by considering material usage, energy consumption, and optional commission. It allows users to set printing parameters, manage printers, calculate total costs, and export/import settings.

The web-based GUI of the project is available at [https://rh45-one.github.io/3DPrintCostCalc/](https://rh45-one.github.io/3DPrintCostCalc/).

## Objectives & Purpose
* Simplify cost estimation for 3D printing tasks.  
* Provide a quick, interactive console interface to manage printers and printing parameters.  
* Enable more accurate planning and budgeting for 3D printing projects.

## Project Structure
```
3DPrintCostCalc/
├── .github/
│   └── workflows/
│       ├── makefile.yml
│       └── static.yml
├── bin/
│   └── ...
├── src/
│   ├── application/
│   │   ├── PrintCostCalculator.java
│   │   └── Printer.java
│   ├── calculations/
│   │   ├── CostCalculator.java
│   │   └── Optimizer.java
│   └──  main/
│       └── Application.java
├── web/
│   ├── css/
│   │   └── styles.css
│   ├── img/
│   │   └── ...
│   ├── js/
│   │   └── scripts.js
│   ├── docs/
│   │   ├── ...
│   │   └── index.html
│   └── index.html
├── 3DPrintCostCalc.jar
├── Makefile
```

## How It Works
1. **User Input:** Prompts for material/energy cost, number of units, and printer settings.  
2. **Printer Management:** Allows adding, listing, and removing printers.  
3. **Calculation Core:** Uses classes in `calculations` to compute total material cost, energy cost, and commissions.  
4. **Distribution Optimization:** Optimally allocates units to printers for minimal cost.  
5. **Export/Import:** Saves or loads current settings from a file.

## Application Flow
1. **Start:** Run the CLI and display a menu.  
2. **Configuration Menu:** Set overall printing parameters (discounts, material cost, etc.).  
3. **Printer Menu:** Add or remove printers.  
4. **Calculation:** Trigger calculations for total cost, including commission.  
5. **Data Persistence:** Optionally export or import configurations.

## Architecture
This application is divided into three main packages, each serving a specific role:
* **main** – Contains the entry point and overall program launch.  
* **application** – Houses the CLI-based interface for user interactions (e.g., adding printers, importing/exporting settings).  
* **calculations** – Provides cost computations, energy usage calculations, and distribution optimization.

## Building & Usage
1. **Compile:**  
   ```bash
   make compile
   ```
2. **Generate Javadoc:**  
   ```bash
   make doc
   ```
3. **Create a JAR:**  
   ```bash
   make jar
   ```
4. **Run the CLI:**  
   ```bash
   java -jar 3DPrintCostCalc.jar
   ```

## Continuous Integration
A GitHub Actions workflow (located in `.github/workflows/makefile.yml`) runs automatically on each push or pull request to the `main` branch. It checks out the code, installs Java, and executes the Make targets (compile, doc, jar) to verify everything builds successfully.

Additionally, another GitHub Actions workflow (located in `.github/workflows/static.yml`) is set up to build and deploy the web-based GUI of the project. This workflow ensures that the web version is always up-to-date and available.
## Future Enhancements
* ~~**Web Version:** Creation of a web-based GUI of the project.~~ ✔️
* **Import/Export Settings:** Allows saving/loading configurations to/from a file.
* **Improve Mobile Interface:** Enhance the web version for better mobile compatibility.
* **Additional Cost Factors:** Integrate advanced maintenance or labor costs as needed.


