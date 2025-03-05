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
  // Sort printers by lowest power usage first
  printerArray.sort((a, b) => a.powerConsumption - b.powerConsumption);

  let distribution = {};
  let remaining = totalUnits;
  while (remaining > 0) {
    for (let p of printerArray) {
      if (remaining <= 0) break;
      const assign = Math.min(remaining, p.bedCapacity);
      distribution[p.nickname] = (distribution[p.nickname] || 0) + assign;
      remaining -= assign;
    }
  }
  return distribution;
}

function calculateEnergyCost(distribution, energyCostPerKwh) {
  let total = 0;
  for (const p of printers) {
    const assigned = distribution[p.nickname] || 0;
    const printerTime = assigned * p.printTimePerUnit;
    total += printerTime * p.powerConsumption * energyCostPerKwh;
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
  Object.keys(distribution).forEach(nick => {
    distInfo += `<li>${nick}: ${distribution[nick]} unit(s)</li>`;
  });

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