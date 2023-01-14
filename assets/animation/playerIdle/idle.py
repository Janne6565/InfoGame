import os

path = "C:/Users/janne/InfoGame/assets/animation/playerIdle/"

for i in range(1, 48):    
    command = 'ren " (' + str(i) + ').png" "(' + str(i) + ').png"'
    print(command)
    os.system(command)
