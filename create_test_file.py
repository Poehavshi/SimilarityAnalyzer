NUMBER_OF_LINES = 20000
with open("uid_page_timestamp.sorted.csv") as input_file, \
    open("test_uid_page_timestamp.sorted.csv",'w') as output_file:
    input_file.readline()
    for i in range(NUMBER_OF_LINES):
        output_file.write(input_file.readline())

