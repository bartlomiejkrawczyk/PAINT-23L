const tileDisplay = document.querySelector(".tile-container")
const keyboard = document.querySelector(".key-container")
const messageDisplay = document.querySelector(".message-container")

let wordle

const getWordle = () => {
    fetch("https://random-word-api.vercel.app/api?words=1&length=5")
        .then(response => response.json())
        .then(json => {
            console.log(json)
            wordle = json[0].toUpperCase()
        })
        .catch(err => console.log(err))
}

getWordle()

const keys = [
    'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P',
    'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L', "ENTER",
    'Z', 'X', 'C', 'V', 'B', 'N', 'M', '≪'
]

const guessRows = [
    ['', '', '', '', ''],
    ['', '', '', '', ''],
    ['', '', '', '', ''],
    ['', '', '', '', ''],
    ['', '', '', '', ''],
    ['', '', '', '', '']
]

let currentRow = 0
let currentTile = 0

let isGameOver = false

guessRows.forEach((guessRow, guessRowIndex) => {
    const rowElement = document.createElement("div")
    rowElement.setAttribute("id", "guessRow-" + guessRowIndex)
    guessRow.forEach((guess, guessIndex) => {
        const tileElement = document.createElement("div")
        tileElement.setAttribute("id", "guessRow-" + guessRowIndex + "-tile-" + guessIndex)
        tileElement.classList.add("tile")
        rowElement.append(tileElement)
    })
    tileDisplay.append(rowElement)
})

keys.forEach(key => {
    const buttonElement = document.createElement("button")
    buttonElement.textContent = key
    buttonElement.setAttribute("id", key)
    buttonElement.addEventListener("click", () => handleClick(key))
    keyboard.append(buttonElement)
})

document.addEventListener("keydown", (event) => {
    const keyName = event.key;
    if (!isGameOver) {
        if (keys.includes(keyName.toUpperCase())) {
            handleClick(keyName.toUpperCase())
        } else if (keyName === "Backspace") {
            deleteLetter()
        }
    }
}, false);

const handleClick = (key) => {
    if (!isGameOver) {
        console.log("Clicked", key + '!')
        if (key === '≪') {
            deleteLetter()
            return
        }
        if (key === "ENTER") {
            checkRow()
            return
        }
        addLetter(key)
    }
}

const addLetter = (letter) => {
    if (currentTile < 5 && currentRow < 6) {
        const tile = document.getElementById("guessRow-" + currentRow + "-tile-" + currentTile)
        tile.textContent = letter
        guessRows[currentRow][currentTile] = letter
        tile.setAttribute("data", letter)
        currentTile++
        console.log("guessRows", guessRows)
    }
}

const deleteLetter = () => {
    if (currentTile > 0) {
        currentTile--
        const tile = document.getElementById("guessRow-" + currentRow + "-tile-" + currentTile)
        tile.textContent = ''
        tile.setAttribute("data", '')
        guessRows[currentRow][currentTile] = ''
        console.log("guessRows", guessRows)
    }
}

const checkRow = () => {
    const guess = guessRows[currentRow].join('')

    if (currentTile > 4) {
        console.log("My guess is", guess, "and the wordle is", wordle + '.')
        flipTile()
        if (guess == wordle) {
            showMessage("You win!")
            isGameOver = true
            return
        } else if (currentRow >= 5) {
            showMessage("You lose!")
            isGameOver = true
            return
        } else {
            showMessage("Nope!")
            currentRow++
            currentTile = 0
        }
    }
}

const showMessage = (message) => {
    const messageElement = document.createElement('p')
    messageElement.textContent = message
    messageDisplay.append(messageElement)
    setTimeout(() => messageDisplay.removeChild(messageElement), 2000)
}

const addColorToKey = (keyLetter, color) => {
    const key = document.getElementById(keyLetter)
    key.classList.add(color)
}

const flipTile = () => {
    const rowTiles = document.getElementById("guessRow-" + currentRow).childNodes
    let checkWordle = wordle
    const guess = []

    rowTiles.forEach(tile => {
        guess.push({ letter: tile.getAttribute("data"), color: "grey-overlay" })
    })

    guess.forEach((guessLetter, guessIndex) => {
        if (guessLetter.letter == wordle[guessIndex]) {
            guessLetter.color = "green-overlay"
            checkWordle = checkWordle.replace(guessLetter.letter, '')
        }
    })

    guess.forEach(guessLetter => {
        if (checkWordle.includes(guessLetter.letter)) {
            guessLetter.color = "yellow-overlay"
            checkWordle = checkWordle.replace(guessLetter.letter, '')
        }
    })

    rowTiles.forEach((tile, index) => {
        setTimeout(() => {
            tile.classList.add("flip")
            tile.classList.add(guess[index].color)
            addColorToKey(guess[index].letter, guess[index].color)
        }, 500 * index)
    })
}
