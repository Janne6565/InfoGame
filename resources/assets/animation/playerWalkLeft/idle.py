import os
os.system("cd playerWalk")

for i in range(1, 22):
    command = 'ren " (' + str(i) + ').png" "(' + str(i) + ').png"'
    print(command)
    os.system(command)
