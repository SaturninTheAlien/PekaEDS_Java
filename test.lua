local editor = require("pk2-editor")

local sector = editor.sector

local res = editor.hello()
print(res)


print(editor.tileProfile.EMPTY)



print(sector)
print(sector.name)
print(sector:getFgTile(1, 2))

print(sector:getFgTileType(1, 2))


print(math.random())

print(math.random(0, 13))


editor.forEachTile(function (x, y)
    print("FG Tile ("..x..","..y..") = " .. sector:getFgTile(x, y))    
end)