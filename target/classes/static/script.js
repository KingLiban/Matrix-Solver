let matrix = [];

document.getElementById("create-matrix").addEventListener("click", () => {
  const rows = parseInt(document.getElementById("rows").value);
  const cols = parseInt(document.getElementById("cols").value);
  domHandler.createMatrix(rows, cols);
});

document.getElementById("upload-matrix").addEventListener("click", () => {
  const fileInput = document.createElement("input");
  fileInput.type = "file";
  fileInput.accept = ".txt";

  fileInput.onchange = (e) => {
    const file = e.target.files[0];
    if (!file) {
      return;
    }

    const reader = new FileReader();

    reader.onload = (e) => {
      const fileContent = e.target.result;
      const rowsData = fileContent.trim().split("\n");
      const rows = rowsData.length;
      const cols = rowsData[0].trim().split(/\s+/).length;

      domHandler.createMatrix(rows, cols);

      matrix = rowsData.map((row) => row.trim().split(/\s+/).map(Number));

      const inputs = document.querySelectorAll(".matrix-input input");
      matrix.flat().forEach((value, index) => {
        inputs[index].value = value;
      });
    };

    reader.readAsText(file);
  };

  fileInput.click();
});

document.getElementById("solve-matrix").addEventListener("click", () => {
  if (matrix.length === 0) {
    domHandler.displayError("Please create or upload a matrix first.");
    return;
  }

  fetch("/matrix", {
    method: "POST",
    body: JSON.stringify(matrix),
    headers: {
      "Content-Type": "application/json",
    },
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error(`Response status: ${response.status}`);
      }
      return response.json();
    })
    .then((data) => {
      domHandler.displayResults(data);
    })
    .catch((error) => {
      console.error("Error", error);
      domHandler.displayError(error.message);
    });
});

const domHandler = (function () {
  function createMatrix(rows, cols) {
    const matrixContainer = document.getElementById("matrix-container");

    while (matrixContainer.firstChild) {
      matrixContainer.removeChild(matrixContainer.firstChild);
    }

    const grid = document.createElement("div");
    grid.className = "matrix-input";
    grid.style.gridTemplateColumns = `repeat(${cols}, 1fr)`;

    matrix = Array.from({ length: rows }, () => Array(cols).fill(0));

    for (let i = 0; i < rows * cols; i++) {
      const input = document.createElement("input");
      input.type = "number";
      input.placeholder = "0";

      const row = Math.floor(i / cols);
      const col = i % cols;

      input.addEventListener("input", () => {
        matrix[row][col] = parseFloat(input.value) || 0;
      });

      grid.appendChild(input);
    }

    matrixContainer.appendChild(grid);
  }

  function displayError(message) {
    const solutionDiv = document.getElementById("solution");
    solutionDiv.innerHTML = `<p style="color: var(--rose-600);">Error: ${message}</p>`;
  }

  function displayMatrix(matrix, container) {
    const table = document.createElement("div");
    table.className = "solution-matrix";
    table.style.gridTemplateColumns = `repeat(${matrix[0].length}, 1fr)`;

    matrix.forEach((row) => {
      row.forEach((cell) => {
        const cellElement = document.createElement("div");
        cellElement.className = "solution-matrix-cell";
        cellElement.textContent = cell.toFixed(2);
        table.appendChild(cellElement);
      });
    });
    container.appendChild(table);
  }

  function displayResults(data) {
    const solutionDiv = document.getElementById("solution");
    solutionDiv.innerHTML = "";

    const generalSolution = document.createElement("div");
    generalSolution.innerHTML = "<h3>General Solution:</h3>";

    if (data.generalSolution === "NO SOLUTION") {
      generalSolution.innerHTML += "<p>NO SOLUTION</p>";
    } else {
      const solution = data.generalSolution;
      const parts = solution.split(" + s_1");
      const constantPart = parts[0];
      const variablePart = parts.length > 1 ? parts[1] : null;

      const constant = constantPart.slice(1, -1).split(", ").map(Number);
      const variable = variablePart
        ? variablePart.slice(1, -1).split(", ").map(Number)
        : null;

      let solutionHTML = "<p>";
      for (let i = 0; i < constant.length; i++) {
        solutionHTML += `x<sub>${i + 1}</sub> = ${constant[i].toFixed(4)}`;
        if (variable && variable[i] !== 0) {
          solutionHTML += ` ${variable[i] >= 0 ? "+" : "-"} ${Math.abs(
            variable[i]
          ).toFixed(4)}s<sub>1</sub>`;
        }
        solutionHTML += "<br>";
      }
      solutionHTML += "</p>";
      generalSolution.innerHTML += solutionHTML;
    }
    solutionDiv.appendChild(generalSolution);

    const rowEchelonForm = document.createElement("div");
    rowEchelonForm.innerHTML = "<h3>Row Echelon Form:</h3>";
    solutionDiv.appendChild(rowEchelonForm);
    displayMatrix(data.rowEchelonForm, rowEchelonForm);

    const reducedRowEchelonForm = document.createElement("div");
    reducedRowEchelonForm.innerHTML = "<h3>Reduced Row Echelon Form:</h3>";
    solutionDiv.appendChild(reducedRowEchelonForm);
    displayMatrix(data.reducedRowEchelonForm, reducedRowEchelonForm);
  }

  return {
    createMatrix,
    displayError,
    displayResults,
  };
})();
