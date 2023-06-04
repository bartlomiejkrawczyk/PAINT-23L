
with open('pl3.txt') as f:
    for line in f:
        with open(f'{len(line)-1}.txt', 'a') as result:
            result.write(line)
