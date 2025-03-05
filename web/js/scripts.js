// JavaScript logic for the 3D Print Cost Calculator application

// Toggle dark/light mode
const themeCheckbox = document.getElementById('theme-checkbox');
themeCheckbox.addEventListener('change', () => {
  document.documentElement.classList.toggle('dark-mode', themeCheckbox.checked);
});

// In-memory storage for printers
const printers = [];

// --- Printer Management ---
document.getElementById('printer-form').addEventListener('submit', (e) => {
  e.preventDefault();
  const nickname = document.getElementById('nickname').value;
  const powerConsumption = parseFloat(document.getElementById('powerConsumption').value);
  const printTimePerUnit = parseFloat(document.getElementById('printTimePerUnit').value);
  const nozzleSize = parseFloat(document.getElementById('nozzleSize').value);
  const bedCapacity = parseInt(document.getElementById('bedCapacity').value, 10);

  printers.push({
    nickname,
    powerConsumption,
    printTimePerUnit,
    nozzleSize,
    bedCapacity
  });
  alert(`Printer "${nickname}" added.`);
  e.target.reset();
});

document.getElementById('list-printers-btn').addEventListener('click', () => {
  if (printers.length === 0) {
    document.getElementById('printers-list').textContent = 'No printers available.';
  } else {
    const listHtml = printers
      .map(p => `• ${p.nickname} | ${p.powerConsumption} kW | ${p.printTimePerUnit} h/unit | ${p.nozzleSize} mm nozzle | ${p.bedCapacity} units`)
      .join('<br>');
    document.getElementById('printers-list').innerHTML = listHtml;
  }
});

// --- Calculation Logic ---
function calculateMaterialCost(totalUnits, materialPerUnit, materialCostPerKg, hasDiscount, discountRate) {
  const totalMaterialKg = (totalUnits * materialPerUnit) / 1000;
  const baseCost = totalMaterialKg * materialCostPerKg;
  return hasDiscount ? baseCost * (1 - discountRate) : baseCost;
}

function optimizeDistribution(totalUnits, printerArray) {
  const distribution = new Map();
  const completionTimes = new Map(); // Tracks batch-based completion times

  // Initialize tracking structures
  printerArray.forEach(printer => {
    distribution.set(printer, 0);
    completionTimes.set(printer, 0);
  });

  for (let i = 0; i < totalUnits; i++) {
    let bestPrinter = null;
    let bestTime = Infinity;

    // Find best printer for current unit
    printerArray.forEach(printer => {
      const currentAssigned = distribution.get(printer);
      const newAssigned = currentAssigned + 1;
      
      // Calculate hypothetical completion time if we add this unit
      const batchesNeeded = Math.ceil(newAssigned / printer.bedCapacity);
      const newCompletion = batchesNeeded * printer.printTimePerUnit;

      if (newCompletion < bestTime) {
        bestTime = newCompletion;
        bestPrinter = printer;
      }
    });

    if (!bestPrinter) break; // Safety check

    // Update distribution and completion time
    distribution.set(bestPrinter, distribution.get(bestPrinter) + 1);
    const updatedAssigned = distribution.get(bestPrinter);
    const updatedBatches = Math.ceil(updatedAssigned / bestPrinter.bedCapacity);
    completionTimes.set(bestPrinter, updatedBatches * bestPrinter.printTimePerUnit);
  }

  // Convert to nickname-based object
  const result = {};
  distribution.forEach((units, printer) => {
    result[printer.nickname] = units;
  });
  
  return result;
}

function calculateEnergyCost(distribution, energyCostPerKwh) {
  let total = 0;
  for (const printerNickname in distribution) {
    const printer = printers.find(p => p.nickname === printerNickname);
    const units = distribution[printerNickname];
    const printerTime = units * printer.printTimePerUnit;
    total += printerTime * printer.powerConsumption * energyCostPerKwh;
  }
  return total;
}

function calculateTotalCost(materialCost, energyCost) {
  return materialCost + energyCost;
}

function calculateTotalCostWithCommission(totalCost, commissionPerUnit, totalUnits) {
  return totalCost + commissionPerUnit * totalUnits;
}

// Main Calculation
document.getElementById('cost-form').addEventListener('submit', (event) => {
  event.preventDefault();

  const totalUnits = parseInt(document.getElementById('units').value, 10);
  const materialPerUnit = parseFloat(document.getElementById('matPerUnit').value);
  const materialCostPerKg = parseFloat(document.getElementById('matCostPerKg').value);
  const hasDiscount = document.getElementById('hasDiscount').checked;
  const discountRate = parseFloat(document.getElementById('discountRate').value);
  const energyCostPerKwh = parseFloat(document.getElementById('energyCost').value);
  const commissionPerUnit = parseFloat(document.getElementById('commission').value);

  if (!printers.length) {
    document.getElementById('result').innerHTML = '<strong>No printers added yet.</strong>';
    return;
  }

  // 1) Distribute units to printers
  const distribution = optimizeDistribution(totalUnits, printers);

  // 2) Calculate costs
  const materialCost = calculateMaterialCost(
    totalUnits, materialPerUnit, materialCostPerKg, hasDiscount, discountRate
  );
  const energyCost = calculateEnergyCost(distribution, energyCostPerKwh);
  const totalCost = calculateTotalCost(materialCost, energyCost);
  const totalWithCommission = calculateTotalCostWithCommission(totalCost, commissionPerUnit, totalUnits);

  // 3) Display distribution and results
  let distInfo = '';
  for (const printerNickname in distribution) {
    distInfo += `<li>${printerNickname}: ${distribution[printerNickname]} unit(s)</li>`;
  }

  document.getElementById('result').innerHTML = `
    <hr>
    <p><strong>Distribution:</strong></p>
    <ul>${distInfo}</ul>
    <p><strong>Material Cost:</strong> $${materialCost.toFixed(2)}</p>
    <p><strong>Energy Cost:</strong> $${energyCost.toFixed(2)}</p>
    <p><strong>Total Cost:</strong> $${totalCost.toFixed(2)}</p>
    <p><strong>Total with Commission:</strong> $${totalWithCommission.toFixed(2)}</p>
  `;
});

// Import/Export Settings
document.getElementById('import-settings-btn').addEventListener('click', () => {
  const fileInput = document.createElement('input');
  fileInput.type = 'file';
  fileInput.accept = '.txt';
  fileInput.onchange = async (event) => {
    const file = event.target.files[0];
    if (file) {
      const text = await file.text();
      const lines = text.split('\n');
      printers.length = 0; // Clear existing printers
      lines.forEach(line => {
        const parts = line.split(',');
        if (parts.length === 5) {
          const nickname = parts[0];
          const powerConsumption = parseFloat(parts[1]);
          const printTimePerUnit = parseFloat(parts[2]);
          const nozzleSize = parseFloat(parts[3]);
          const bedCapacity = parseInt(parts[4], 10);
          printers.push({ nickname, powerConsumption, printTimePerUnit, nozzleSize, bedCapacity });
        }
      });
      alert('Settings imported successfully.');
    }
  };
  fileInput.click();
});

document.getElementById('export-settings-btn').addEventListener('click', () => {
  const lines = printers.map(p => `${p.nickname},${p.powerConsumption},${p.printTimePerUnit},${p.nozzleSize},${p.bedCapacity}`);
  const blob = new Blob([lines.join('\n')], { type: 'text/plain' });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = 'settings.txt';
  a.click();
  URL.revokeObjectURL(url);
});