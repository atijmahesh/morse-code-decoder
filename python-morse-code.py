morse_code_dict = {
    '.-': 'A', '-...': 'B', '-.-.': 'C', '-..': 'D', '.': 'E', '..-.': 'F', 
    '--.': 'G', '....': 'H', '..': 'I', '.---': 'J', '-.-': 'K', '.-..': 'L', 
    '--': 'M', '-.': 'N', '---': 'O', '.--.': 'P', '--.-': 'Q', '.-.': 'R', 
    '...': 'S', '-': 'T', '..-': 'U', '...-': 'V', '.--': 'W', '-..-': 'X', 
    '-.--': 'Y', '--..': 'Z', '-----': '0', '.----': '1', '..---': '2', 
    '...--': '3', '....-': '4', '.....': '5', '-....': '6', '--...': '7', 
    '---..': '8', '----.': '9'
}

running = True;
while running:
    morse_code = input("Please input a message in Morse Code: ")

    morse_arr = morse_code.split(' ')

    message = ""
    success = True
    for item in morse_arr:
        if item not in morse_code_dict:
            success = False
            break
        message += morse_code_dict[item]

    if success:
        print(f"The decoded message is: {message}")
    else:
        print("This message was invalid and not in morse code.")
    
    ip = input("Do you want to go again? Type y/n: ")
    if ip.lower() == 'n':
        running = False
