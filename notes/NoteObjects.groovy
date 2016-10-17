
keys='+ ++ + ++ + '
letters=[*'a'..'g']*.toUpperCase()
//letters = letters[2..-1] + letters[0..<2] // C - B

key=0
halfStep=0

list = keys.collect {
   
   if (it == '+') names=[letters[key++]]
   else names=[letters[key-1]+'#',letters[key%7]+'b']
   
   flatName=names[-1]
   sharpName=names[0]

   freq = 440 * 2 ** (halfStep / 12)
   //println names+'; freq='+freq
   
   halfStep++
   "    new Note(\"$flatName\",\"$sharpName\",$freq)"

}.join(',\n')


