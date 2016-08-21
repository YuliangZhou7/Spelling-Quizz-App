package spellingquizz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JOptionPane;

/**
 * 
 * @author Yuliang Zhou yzho746
 *
 */
public class SpellingStatsModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private ArrayList<Word> _wordList;

	private ArrayList<String> _failedList;

	private boolean _testMode;
	private String[] _testWords;

	private boolean _firstAttempt;
	private boolean _checkWord;
	private int _position;
	private boolean _reviewMode;
	private boolean _doubleFailRepeat = false;

	public SpellingStatsModel(){
		_wordList = new ArrayList<Word>();
		_failedList = new ArrayList<String>();
		_testMode = false;

		_position = 0;
		_firstAttempt = true;
		_checkWord = false;
		_reviewMode = false;
	}

	/**
	 * Sets the three random words for the list of words to test
	 */
	public void startNewTest(){
		_testMode = true;
		_testWords = new String[3];
		Collections.shuffle(_wordList);
		for(int i=0; i< 3;i++){
			_testWords[i] = _wordList.get(i).toString();
		}
		_position = 0;
		_firstAttempt = true;
		_checkWord = false;
	}

	/**
	 * Sets the 3 (or less) random words from the failed list
	 */
	public void startReviewTest(){
		_testMode = true;
		Collections.shuffle(_failedList);
		if(_failedList.size()<3){//less than 3 words
			_testWords = new String[_failedList.size()];
			for(int i=0;i<_failedList.size();i++){
				_testWords[i] = _failedList.get(i).toString();
			}
		}else{//3 or more words
			_testWords = new String[3];
			for(int i=0;i<3;i++){
				_testWords[i] = _failedList.get(i).toString();
			}
		}
		_reviewMode = true;
		_position = 0;
		_firstAttempt = true;
		_checkWord = false;
	}

	/**
	 * testLogic is called each time "Enter" is pressed. Checks if user input is the same
	 * as the word from the spelling list. 
	 * Reads out the word to be tested and whether the user entered the correct word or not.
	 * @param String userInput
	 * @return
	 */
	public String testLogic(String userInput){
		// Stop if there are no words from failed list
		if(_testWords.length == 0){
			_testMode = false;
			_reviewMode = false;
			return "No words to test.\n===============DONE=================\n";
		}

		//let user enter word again (3rd time) and check
		if(_reviewMode && _doubleFailRepeat){
			if( userInput.trim().toLowerCase().equals(_testWords[_position]) ){
				festivalRead("Correct.");
			}
			_doubleFailRepeat = false;
			_position++;
			_checkWord = false;
			_firstAttempt = true;
		}

		//CHECK
		if( _checkWord ){
			if( _firstAttempt ){ 
				if( userInput.trim().toLowerCase().equals(_testWords[_position]) ){ 	// MASTERED
					incrementMastered(_testWords[_position]);
					if(_reviewMode){//if in review mode removed word from failed list.
						removedFailedWord(_testWords[_position]);
					}
					festivalRead("Correct.");
					_position++;
					_checkWord = false;
					_firstAttempt = true;
					//go to READ (next word)
				}else{
					_firstAttempt = false;
					// go to second attempt read
				}
			}else{// SECOND ATTEMPT
				if( userInput.trim().toLowerCase().equals(_testWords[_position]) ){		// FAULTED
					incrementFaulted(_testWords[_position]);
					if(_reviewMode){//if in review mode removed word from failed list.
						removedFailedWord(_testWords[_position]);
					}
					festivalRead("Correct.");
					_position++;
					_checkWord = true;
					_firstAttempt = true;
					//go to READ (next word)
				}else{																	// FAILED
					incrementFailed(_testWords[_position]);
					addFailedWord(_testWords[_position]);
					festivalRead("Incorrect");
					if(_reviewMode){//if in review mode and failed again.
						int dialogResult = JOptionPane.showConfirmDialog (null, "Do you wish to hear the spelling?","Confirm",JOptionPane.YES_NO_OPTION);
						if(dialogResult == JOptionPane.YES_OPTION){
							String[] letters = _testWords[_position].split("");
							for(int i=0;i<letters.length;i++){
								festivalRead(letters[i]);
							}
							_doubleFailRepeat = true;
							return "Repeat after me: \n";
						}
					}//end review mode
					_position++;
					_checkWord = true;
					_firstAttempt = true;
					//go to READ (next word)
				}
			}//end ATTEMPT
		}//end CHECK

		if(_position>_testWords.length-1){
			_testMode = false;
			_reviewMode = false;
			return "===============DONE=================\n";
		}
		//READ OUT WORDS
		if( _firstAttempt ){
			festivalRead("Please spell " + _testWords[_position]);
			_checkWord = true;
			return ("Please spell word " + (_position+1) + " of " + _testWords.length + ": \n");

		}else{//Second attempt
			festivalRead("Incorrect. Please try again. " + _testWords[_position] + " ..  " + _testWords[_position]);
			_checkWord = true;
			return "Incorrect. Please try again: \n";

		}



	}


	/**
	 * Uses festival to read a message to the user. Waits for process to finish before
	 * continuing with other processes.
	 */
	public void festivalRead(String message){
		String cmd = "echo " + message + " | festival --tts";
		ProcessBuilder builder = new ProcessBuilder("/bin/bash","-c",cmd);
		try {
			Process process = builder.start();
			process.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds a new Word given as a String only if it is not already contained in the spelling list.
	 * @param word
	 */
	public void addNewWord(String word){
		boolean containsWord = false;
		for(int i=0;i<_wordList.size();i++){
			if(_wordList.get(i).toString().equals(word)){
				containsWord = true;
				break;
			}
		}
		if(!containsWord){
			Word newWord = new Word(word);
			_wordList.add(newWord);
		}
	}

	/**
	 * Adds the word (given as a String) to the list of failed words only if it doesn't already
	 * contain the failed word.
	 * @param failedWord
	 */
	public void addFailedWord(String failedWord){
		if(!_failedList.contains(failedWord)){
			_failedList.add(failedWord);
		}
	}

	/**
	 * Removes a word from the failed list if it is in the list.
	 * @param failedWord
	 */
	public void removedFailedWord(String failedWord){
		_failedList.remove(failedWord);
	}

	public int getNumWords(){
		return _wordList.size();
	}

	public Word getWordAt(int index) {
		return _wordList.get(index);
	}

	public void incrementMastered(String word){
		for(int i=0; i < _wordList.size();i++){
			Word currentWord = _wordList.get(i);
			if( currentWord.toString().equals(word) ){
				int count = currentWord.getMastered();
				count++;
				currentWord.setMastered(count);
				break;
			}
		}
	}

	public void incrementFaulted(String word){
		for(int i=0; i < _wordList.size();i++){
			Word currentWord = _wordList.get(i);
			if( currentWord.toString().equals(word) ){
				int count = currentWord.getFaulted();
				count++;
				currentWord.setFaulted(count);
				break;
			}
		}
	}

	public void incrementFailed(String word){
		for(int i=0; i < _wordList.size();i++){
			Word currentWord = _wordList.get(i);
			if( currentWord.toString().equals(word) ){
				int count = currentWord.getFailed();
				count++;
				currentWord.setFailed(count);
				break;
			}
		}
	}

	/**
	 * resets all Mastered, Faulted, and Failed for all words in word list.
	 * Clears failed list.
	 */
	public void resetStats() {
		for (Word item : _wordList){
			item.setFailed(0);
			item.setFaulted(0);
			item.setMastered(0);
		}
		_failedList = new ArrayList<>();
	}

	/**
	 * Sort the word list alphabetically
	 * Used when displaying statistics in a JTable
	 */
	public void sortAlphabetically() {
		Collections.sort(_wordList, new Comparator<Word>() {
			@Override
			public int compare(Word word1, Word word2) {
				return  word1.toString().compareTo(word2.toString());
			}
		});
	}

	public boolean testMode() {
		return _testMode ;
	}

}
