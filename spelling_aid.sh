#!/bin/bash

#Author: Yuliang Zhou

#functions

print_menu(){
echo -e "Please select from one of the following options:

   (n)ew spelling quiz
   (r)eview mistakes
   (v)iew statistics
   (c)lear statistics
   (q)uit application
   "
read -p "Enter a selection [n/r/v/c/q]:" SELECT
}

ask_for_correct_input(){
#keeps asking for input until a correct option is given
while [ "$SELECT" != "n" ] && [ "$SELECT" != "r" ] && [ "$SELECT" != "v" ] \
&& [ "$SELECT" != "c" ] && [ "$SELECT" != "q" ]; do
  echo "Invalid input. Please try again."
  print_menu
done
}

new_spelling_quiz(){

#check if we are in new quizz or review mode
if [ "$1" == "r" ] ; then
    if [ $(cat $FAILEDLIST | wc -l) -lt 3 ] ; then
      #less than 3 words failed
      wordArray=( $(shuf -n $(cat $FAILEDLIST | wc -l) $FAILEDLIST) )
    else
      #3 or more words failed
      wordArray=( $(shuf -n 3 $FAILEDLIST) )
    fi
else 
    wordArray=( $(shuf -n 3 $WORDLIST) )
fi

count=1

for i in ${wordArray[@]} ; do

    echo "Please spell. $i" | festival --tts
    read -p "Spell word $count of ${#wordArray[@]}: " userSpelling
    #convert to lower case
    userINPUT=${userSpelling,,}
    
    if [ $i != $userINPUT ] || [ -z "$userSpelling" ] ; then
    
      echo "Incorrect, try once more .. $i ... $i" | festival --tts
      read -p "   Incorrect, try once more: " userSpelling
      userINPUT=${userSpelling,,}
      
	if [ $i != $userINPUT ] || [ -z "$userSpelling" ]; then
	  #failed
	  echo "Incorrect." | festival --tts
	  echo "$i F" >> $STATSLIST
	  #Check if word is already in list
	  if  ! grep -q "$i" $FAILEDLIST ; then
	    echo $i >> $FAILEDLIST
	  fi
	else
	  #faulted
	  echo "Correct." | festival --tts
	  echo "$i P" >> $STATSLIST
	  #if in review mode delete from failed list
	  if [ "$1" == "r" ] ; then
	    sed -i /$i/d $FAILEDLIST
	  fi
	fi
	
    else
      #mastered
      echo "Correct." | festival --tts
      echo "$i M" >> $STATSLIST
      #if in review mode delete from failed list
      if [ "$1" == "r" ] ; then
	sed -i /$i/d ./$FAILEDLIST
      fi
    fi
    
    count=$(($count+1))
    
done

}



#==========================================================================================#



echo -e "==============================================================
Welcome to the Spelling Aid
=============================================================="

#variables
WORDLIST=.sortedWordlist.txt
FAILEDLIST=.failedWordList.txt
STATSLIST=.statsList.txt

touch $WORDLIST
touch $STATSLIST
touch $FAILEDLIST

#sort the word list alphabetically
sort wordlist > $WORDLIST

while true ; do
  print_menu
  ask_for_correct_input
  if [ $SELECT == "q" ] ; then
    break
  elif [ $SELECT == "n" ] ; then
    echo "Starting new quizz..."
    new_spelling_quiz 
  elif [ $SELECT == "r" ] ; then
    echo "Reviewing errors..."
    #No failed words. Return to main menu
    if [[ -s $FAILEDLIST ]] ; then
      new_spelling_quiz r
    else
      echo "No mistakes to display :)"
    fi
    elif [ $SELECT == "v" ] ; then
    echo "Now displaying your stats..."
    if [ -s $STATSLIST ] ; then
      #for each word in wordList. count no. of mastered failed and faulted
      while read WORD ; do
	mastered=$(grep -c "$WORD M" $STATSLIST)
	faulted=$(grep -c "$WORD P" $STATSLIST)
	failed=$(grep -c "$WORD F" $STATSLIST)
	if [ $mastered -ne 0 ] || [ $faulted -ne 0 ] || [ $failed -ne 0 ]; then
	  echo "Word:$WORD	Mastered: $mastered	Faulted: $faulted	Failed: $failed"
	fi
      done <$WORDLIST
    else
      echo "Sorry no results to display :("
    fi
  elif [ $SELECT == "c" ] ; then
    cp /dev/null $FAILEDLIST
    cp /dev/null $STATSLIST
    echo "History has been sucessfully cleared"
  fi
done




echo 'Thanks for playing. Come back anytime!'






