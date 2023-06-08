import codecs
import random
from unidecode import unidecode

random.seed(10)


def get_english_words(file_read):
    # words source: https://copylists.com/words/list-of-4-letter-words/
    file = ""
    i = 0
    with open(file_read, 'r') as fp:
        for line in fp:
            nr, word = line.split(',')
            text = f"INSERT INTO words(word_id, language_id, word_length, word_number, word) VALUES ({int(nr)+1000}, 1, 4, {i}, '{word[:-1]}');\n"
            file += text
            i += 1
    return file


def get_polish_words(file_read, nr_letters, start=2000, max_amount=500):
    # words source: https://sjp.pl/sl/growe/
    file = ""
    i = 0
    with codecs.open(file_read, 'r', "utf-8") as fp:
        for line in fp.readlines():
            # random values in order to get more diverse list -> not only words starting with 'a'
            if len(line[:-2]) == nr_letters and random.randint(0, 80) % 80 == 0:
                text = f"INSERT INTO words(word_id, language_id, word_length, word_number, word) VALUES ({start}, 2, {nr_letters}, {i}, '{unidecode(line[:-2])}');\n"
                file += text
                start += 1
                i += 1
            if i > max_amount:
                break
    return file


if __name__ == "__main__":
    filename = "slowa.txt"
    text = get_polish_words(filename, 5)
    # filename = "4_letter_words.csv"
    # text = get_english_words(filename)
    with open("words_inserts.sql", 'a') as text_file:
        n = text_file.write(text)
