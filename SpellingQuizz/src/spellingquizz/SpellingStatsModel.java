package spellingquizz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 
 * @author Yuliang Zhou yzho746
 *
 */
public class SpellingStatsModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ArrayList<Word> _wordList;

	private ArrayList<String> _failedList;

	public int testCounter;

	public SpellingStatsModel(){
		_wordList = new ArrayList<Word>();
		_failedList = new ArrayList<String>();
		testCounter = 0;
	}



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

	public int getNumWords(){
		return _wordList.size();
	}

	public void printWords(){
		System.out.println(Arrays.toString(_wordList.toArray()));
	}



	public Word getWordAt(int index) {
		return _wordList.get(index);
	}

	public String[] getThreeRandomWords(){
		String[] randomWords = new String[3];
		Collections.shuffle(_wordList);
		for(int i=0; i< 3;i++){
			randomWords[i] = _wordList.get(i).toString();
		}
		return randomWords;
	}

	/**
	 * resets all Mastered, Faulted, and Failed for all words in word list.
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
	 */
	public void sortAlphabetically() {
		Collections.sort(_wordList, new Comparator<Word>() {
	        @Override
	        public int compare(Word word2, Word word1) {
	            return  word2.toString().compareTo(word1.toString());
	        }
	    });
	}

}
