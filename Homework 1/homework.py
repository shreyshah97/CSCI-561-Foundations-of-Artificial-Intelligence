from queue import Queue, PriorityQueue

directions = [(1, 1), (1, -1), (-1, 1), (-1, -1), (1, 0), (0, 1), (-1, 0), (0, -1)]

def read_file(filename="input.txt"):
    f = open(filename, "r")
    algo = f.readline().strip()
    W, H = f.readline().split()
    W = int(W)
    H = int(H)
    start = f.readline().split()
    start = (int(start[1]), int(start[0]))
    stamina = int(f.readline())
    lodges_count = int(f.readline())
    
    lodges = []
    for _ in range(lodges_count):
        lodges.append(f.readline().split())
    lodges = [(int(ele[1]), int(ele[0])) for ele in lodges]
    
    grid = []
    for _ in range(H):
        temp = f.readline().split()
        temp = [int(ele) for ele in temp]
        grid.append(temp)
    f.close()
    return algo, W, H, start, stamina, lodges, grid

def is_valid_position(x, y, W, H):
    return x >= 0 and x < H and y >= 0 and y < W

def is_valid_move(curr_elevation, new_elevation, stamina, momentum=0):
    if new_elevation < 0:
        return abs(curr_elevation) >= abs(new_elevation)
    else:
        return new_elevation <= abs(curr_elevation) + stamina + momentum

def bfs(W, H, start, stamina, lodges, grid):
    f = open("output.txt", "w")
    path_map = [["" for _ in range(W)] for _ in range(H)]
        
    for goal in lodges:
        line = "\n"
        if (goal==lodges[-1]):
            line = ""
        if(path_map[goal[0]][goal[1]] != ""):
            f.write(path_map[goal[0]][goal[1]]+line)
            continue
        
        flag = False
        visited = [[False for _ in range(W)] for _ in range(H)]
        queue = Queue()
        queue.put([start, 0, str(start[1])+","+str(start[0])])
        while not queue.empty():
            pos, cost, path = queue.get()
            if(visited[pos[0]][pos[1]] == True):
                continue
            visited[pos[0]][pos[1]] = True
            if(path_map[pos[0]][pos[1]] == ""):
                path_map[pos[0]][pos[1]] = path
            if(pos == goal):
                f.write(path+line)
                flag = True
                break
            for direction in directions:
                x = pos[0] + direction[0]
                y = pos[1] + direction[1]
                if is_valid_position(x, y, W, H) and visited[x][y] == False and is_valid_move(grid[pos[0]][pos[1]], grid[x][y], stamina):
                    queue.put([(x, y), cost+1, path+" "+str(y)+","+str(x)])

        if flag == False:
            f.write("FAIL"+line)
    f.close()
    return

def ucs(W, H, start, stamina, lodges, grid):
    f = open("output.txt", "w")
    path_map = [["" for _ in range(W)] for _ in range(H)]

    for goal in lodges:
        line = "\n"
        if (goal==lodges[-1]):
            line = ""
        if(path_map[goal[0]][goal[1]] != ""):
            f.write(path_map[goal[0]][goal[1]]+line)
            continue
        
        flag = False
        visited = [[False for _ in range(W)] for _ in range(H)]
        queue = PriorityQueue()
        queue.put([0, start, str(start[1])+","+str(start[0])])
        while queue.qsize():
            cost, pos, path = queue.get()
            if(visited[pos[0]][pos[1]] == True):
                continue
            visited[pos[0]][pos[1]] = True
            if(path_map[pos[0]][pos[1]] == ""):
                path_map[pos[0]][pos[1]] = path
            if(pos == goal):
                f.write(path + line)
                flag = True
                break
            for i, direction in enumerate(directions):
                x = pos[0] + direction[0]
                y = pos[1] + direction[1]
                if is_valid_position(x, y, W, H) and visited[x][y] == False and is_valid_move(grid[pos[0]][pos[1]], grid[x][y], stamina):
                    move_cost = 10
                    if i<4:
                        move_cost = 14
                    queue.put([cost + move_cost, (x, y), path+" "+str(y)+","+str(x)])

        if flag == False:
            f.write("FAIL"+line)
    f.close()
    return

def calc_heuristic_cost(pos, goal):
    return int(((pos[0] - goal[0])**2 + (pos[1] - goal[1])**2)**0.5)

def calc_elevation_cost(curr_elevation, new_elevation, momentum):
    return max(0, (abs(new_elevation) - (abs(curr_elevation) + momentum))) 

def calc_momentum(curr_elevation, new_elevation):
    return max(0, abs(curr_elevation) - abs(new_elevation))

def astar(W, H, start, stamina, lodges, grid):
    f = open("output.txt", "w")
    for goal in lodges:
        flag = False
        line = "\n"
        if (goal==lodges[-1]):
            line = ""
        visited = [[False for _ in range(W)] for _ in range(H)]
        momentum_map = [[-1 for _ in range(W)] for _ in range(H)]
        queue = PriorityQueue()
        queue.put([0, 0, 0, start, str(start[1])+","+str(start[0])])
        
        while queue.qsize():
            _, cost, momentum, pos, path = queue.get()
            momentum = abs(momentum)
            if momentum <= momentum_map[pos[0]][pos[1]]:
                continue
            momentum_map[pos[0]][pos[1]] = momentum

            if pos == goal:
                f.write(path+line)
                flag = True
                break
            visited[pos[0]][pos[1]] = True
            for i, direction in enumerate(directions):
                x = pos[0] + direction[0]
                y = pos[1] + direction[1]

                if is_valid_position(x, y, W, H) and is_valid_move(grid[pos[0]][pos[1]], grid[x][y], stamina, momentum):
                    new_momentum = calc_momentum(grid[pos[0]][pos[1]], grid[x][y])
                    if (visited[x][y] == False or new_momentum > momentum_map[x][y]) :
                        move_cost = 10
                        if i<4:
                            move_cost = 14
                        heuristic_cost = calc_heuristic_cost((x, y), goal)
                        elevation_cost = calc_elevation_cost(grid[pos[0]][pos[1]], grid[x][y], momentum)
                        new_cost_with_heuristic = cost + move_cost + heuristic_cost + elevation_cost
                        new_cost = cost + move_cost + elevation_cost
                        
                        queue.put([new_cost_with_heuristic, new_cost, -new_momentum, (x, y), path+" "+str(y)+","+str(x)])

        if flag == False:
            f.write("FAIL"+line)
    f.close()
    return

def main():
    algo, W, H, start, stamina, lodges, grid = read_file()
    if algo == "BFS":
        bfs(W, H, start, stamina, lodges, grid)
    elif algo == "UCS":
        ucs(W, H, start, stamina, lodges, grid)
    else:
        astar(W, H, start, stamina, lodges, grid)
    return

if __name__ == "__main__":
    main()
    