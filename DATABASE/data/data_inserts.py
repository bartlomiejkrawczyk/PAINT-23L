from os import listdir, remove, path

LANGUAGES = {
    "wordle": 0,
    "en": 1,
    "pl": 2,
    "it": 3,
}


def list_files(language: str) -> list[str]:
    return listdir(f"{language}/")


def read_words(directory: str, file: str) -> list[str]:
    with open(f"{directory}/{file}") as file_handle:
        return [line.strip() for line in file_handle]


def format_word(word_id: int, language_id: int, word_num: int, word: str) -> str:
    return f"INSERT INTO words(word_id, language_id, word_length, word_number, word) VALUES ({word_id}, {language_id}, {len(word)}, {word_num}, '{word}');\n"


if __name__ == "__main__":
    if path.exists("words_inserts.sql"):
        remove("words_inserts.sql")
    i = 0
    for key, id in LANGUAGES.items():
        for file in list_files(key):
            for word_num, word in enumerate(read_words(key, file)):
                sql = format_word(i, id, word_num, word)
                with open("words_inserts.sql", 'a') as word_inserts:
                    word_inserts.write(sql)
                i += 1
