input_file = open("uid_page_timestamp.sorted.csv",'r')
output_file = open("test_uid_page_timestamp.sorted.csv", 'w')
for i in range(5000):
    output_file.write(input_file.readline())
input_file.close()
output_file.close()
