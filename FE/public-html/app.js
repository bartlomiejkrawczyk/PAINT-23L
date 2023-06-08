const tileDisplay = document.querySelector(".tile-container")
const keyboard = document.querySelector(".key-container")
const messageDisplay = document.querySelector(".message-container")

let bearer
let session
let letters
let error

async function authorize() {
    const credential = navigator.credentials.get()
    if (credential.name != null && credential.password != null) {
        await getUserAuth(credential.name, credential.password)
    } else {
        await getAnonymousAuth()
    }
}

async function getAnonymousAuth() {
    const response = await fetch("http://localhost:7788/anonymous", {
        method: "GET",
        headers: {
            "accept": "*/*"
        }
    })
    const jsonData = await response.json()
    bearer = jsonData.tokenValue
}

async function getUserAuth(login, password) {
    const response = await fetch("http://localhost:7788/login", {
        method: "POST",
        headers: {
            "accept": "*/*"
        },
        body: {
            "login": login,
            "password": password
        }
    })
    const jsonData = await response.json()
    bearer = jsonData.tokenValue
}

async function register(login, password, confirmPassword) {
    const response = await fetch("http://localhost:7788/register", {
        method: "POST",
        headers: {
            "accept": "*/*"
        },
        body: {
            "login": login,
            "password": password,
            "confirmPassword": confirmPassword
        }
    })
    const jsonData = await response.json()
}

async function initSession() {
    await authorize()
    const response = await fetch("http://localhost:7777/wordle?languageId=0", {
        method: "GET",
        headers: {
            "Authorization": `Bearer ${bearer}`,
            "accept": "*/*",
            "wordLength": 5
        }
    })
    const jsonData = await response.json()
    session = jsonData.id
}

async function guessWordle(wordle) {
    const response = await fetch("http://localhost:7777/wordle", {
        method: "POST",
        headers: {
            "Authorization": `Bearer ${bearer}`,
            "accept": "*/*",
            "sessionId": session,
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            "guess": wordle
        })
    })
    const jsonData = await response.json()
    letters = jsonData.currentGuess
    error = jsonData.error
}

initSession()

const letterState = {
    unknown: "UNKNOWN",
    absent: "LETTER_ABSENT",
    correct_wrong_spot: "CORRECT_LETTER_INCORRECT_PLACE",
    correct: "CORRECT_LETTER_CORRECT_PLACE"
}

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

const forbidListening = [
    "#login",
    "#register"
]

document.addEventListener("keydown", (event) => {
    for (const forbiddenURL of forbidListening) {
        if (window.location.href.indexOf(forbiddenURL) > -1) {
            return
        }
    }
    const keyName = event.key
    if (!isGameOver) {
        if (keys.includes(keyName.toUpperCase())) {
            handleClick(keyName.toUpperCase())
        } else if (keyName === "Backspace") {
            deleteLetter()
        }
    }
}, false)

const handleClick = (key) => {
    if (!isGameOver) {
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
    }
}

const deleteLetter = () => {
    if (currentTile > 0) {
        currentTile--
        const tile = document.getElementById("guessRow-" + currentRow + "-tile-" + currentTile)
        tile.textContent = ''
        tile.setAttribute("data", '')
        guessRows[currentRow][currentTile] = ''
    }
}

const checkWin = (letters) => {
    if (letters[0] == letterState.correct) {
        return (new Set(letters)).size === 1
    } else {
        return false
    }
}

const checkRow = async () => {
    if (currentTile > 4) {
        const guess = guessRows[currentRow].join('')

        await guessWordle(guess.toLowerCase())
        if (error !== undefined) {
            showMessage("Word not found!")
        }

        flipTile(letters)

        if (checkWin(letters)) {
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
    } else {
        showMessage("Not enough letters!")
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

const flipTile = (letters) => {
    const rowTiles = document.getElementById("guessRow-" + currentRow).childNodes
    const guess = []

    rowTiles.forEach(tile => {
        guess.push({ letter: tile.getAttribute("data"), color: "grey-overlay" })
    })

    letters.forEach((letter, index) => {
        if (letter == letterState.correct) {
            guess[index].color = "green-overlay"
        } else if (letter == letterState.correct_wrong_spot) {
            guess[index].color = "yellow-overlay"
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

// LOGIN POP UP

const submitLogin = document.getElementById("login-submit")

submitLogin.addEventListener("click", (e) => {
    e.preventDefault()
    const email = document.getElementById("login-email").value
    const password = document.getElementById("login-password").value

    const credential = new PasswordCredential({
        id: email,
        name: email,
        password: password,
    });

    navigator.credentials.store(credential)
        .then(() => authorize())
        .then(() => window.location.href = '/#');
})

// REGISTRATION POP UP

const submitRegister = document.getElementById("register-submit")

submitRegister.addEventListener("click", (e) => {
    e.preventDefault()
    const email = document.getElementById("register-email").value
    const password = document.getElementById("register-password").value
    const confirmPassword = document.getElementById("register-confirm-password").value

    const credential = new PasswordCredential({
        id: email,
        name: email,
        password: password,
    });

    register(email, password, confirmPassword)
        .then(() => navigator.credentials.store(credential)
        .then(() => authorize()))
        .then(() => window.location.href = '/#');
})

// THEME TOGGLE

const themeSwitch = document.getElementById("theme-toggle")

let themeSwitched = false

themeSwitch.addEventListener("click", function handleClick() {
    if (themeSwitched) {
        document.getElementById("theme-icon").textContent = "dark_mode"
    } else {
        document.getElementById("theme-icon").textContent = "light_mode"
    }
    themeSwitched = !themeSwitched

    const siteBody = document.body
    siteBody.classList.toggle("light-mode")
})
