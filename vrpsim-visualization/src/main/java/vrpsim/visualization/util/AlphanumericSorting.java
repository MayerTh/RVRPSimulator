package vrpsim.visualization.util;

import java.util.Comparator;

import vrpsim.core.model.structure.IVRPSimulationModelStructureElement;

/**
 * @author Kushal Paudyal
 * www.icodejava.com
 * Last Modified On 16th July 2009
 *
 * This class is used to sort alphanumeric strings.
 *
 * My solution is inspired from a similar C# implementation available at
 * http://dotnetperls.com/alphanumeric-sorting written by Sam Allen
 */
public class AlphanumericSorting implements Comparator<IVRPSimulationModelStructureElement> {

	 /**
     * The compare method that compares the alphanumeric strings
     */
    public int compare(IVRPSimulationModelStructureElement firstObjToCompare, IVRPSimulationModelStructureElement secondObjToCompare) {
        String firstString = firstObjToCompare.getVRPSimulationModelElementParameters().getId();
        String secondString = secondObjToCompare.getVRPSimulationModelElementParameters().getId();
 
        if (secondString == null || firstString == null) {
            return 0;
        }
 
        int lengthFirstStr = firstString.length();
        int lengthSecondStr = secondString.length();
 
        int index1 = 0;
        int index2 = 0;
 
        while (index1 < lengthFirstStr && index2 < lengthSecondStr) {
            char ch1 = firstString.charAt(index1);
            char ch2 = secondString.charAt(index2);
 
            char[] space1 = new char[lengthFirstStr];
            char[] space2 = new char[lengthSecondStr];
 
            int loc1 = 0;
            int loc2 = 0;
 
            do {
                space1[loc1++] = ch1;
                index1++;
 
                if (index1 < lengthFirstStr) {
                    ch1 = firstString.charAt(index1);
                } else {
                    break;
                }
            } while (Character.isDigit(ch1) == Character.isDigit(space1[0]));
 
            do {
                space2[loc2++] = ch2;
                index2++;
 
                if (index2 < lengthSecondStr) {
                    ch2 = secondString.charAt(index2);
                } else {
                    break;
                }
            } while (Character.isDigit(ch2) == Character.isDigit(space2[0]));
 
            String str1 = new String(space1);
            String str2 = new String(space2);
 
            int result;
 
            if (Character.isDigit(space1[0]) && Character.isDigit(space2[0])) {
                Integer firstNumberToCompare = new Integer(Integer
                        .parseInt(str1.trim()));
                Integer secondNumberToCompare = new Integer(Integer
                        .parseInt(str2.trim()));
                result = firstNumberToCompare.compareTo(secondNumberToCompare);
            } else {
                result = str1.compareTo(str2);
            }
 
            if (result != 0) {
                return result;
            }
        }
        return lengthFirstStr - lengthSecondStr;
    }
	
}
