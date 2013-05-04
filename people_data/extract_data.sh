#!/bin/bash
#shopt -s globstar

for authorname in *
do
  if [[ ! -f "$authorname" ]]
  then
      continue
  fi
  #outputfile=$authorname."content"
  authorids=$(grep  "$authorname" /home/adarshms/academics/cs410/project/artnet/authors.txt | awk 'BEGIN { FS = "\t" } ; {print $1}')
  for authorid in $authorids
  do
    echo "---------------------"
    echo $authorname " " $authorid
    paperids=$(grep -w "$authorid" /home/adarshms/academics/cs410/project/artnet/author_paper.txt | awk 'BEGIN { FS = "\t" } ; {print $2}')
    for paperid in $paperids
    do
      #echo $paperid
      papers=$(grep -w "$paperid" /home/adarshms/academics/cs410/project/artnet/papers.txt | awk 'BEGIN { FS = "\t" } ; {$1="";print $0}')
      echo $papers >> "$authorname"
    done
    break
  done
done

