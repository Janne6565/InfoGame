import os

for i in range(1, 27):
    command = 'ren " (' + str(i) + ').png" "' + str(i) + '.png"'
    print(command)
    os.system(command)
