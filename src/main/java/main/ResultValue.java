/*
 * Written by Yong Li (liyong@ios.ac.cn)
 * This file is part of the Buchi.
 * 
 * Buchi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Buchi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Buchi. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package main;

public enum ResultValue {
	
	
	OK,        // OK for non-return or object return
	NULL,      // null for boolean or object return
	FALSE,     // false for boolean return
	TRUE,      // true for boolean return
	
	// runtime exceptions or errors
	EXE_UNKNOWN,
	EXE_TIMEOUT,
	EXE_MEMOOUT;

	
	public String toString() {
		
		switch(this) {
		case OK:
			return "ok";
		case NULL:
			return "null";
		case FALSE:
			return "false";
		case TRUE:
			return "true";
		case EXE_UNKNOWN:
			return "unknown";
		case EXE_TIMEOUT:
			return "time-out";
		case EXE_MEMOOUT:
			return "memory-out";
		default:
			assert false : "Unknown value for ResultValue";
		}
		return null;
	}
	
	public boolean isNormal() {
        switch (this) {
        case OK:
            return true;
        case FALSE:
            return true;
        case TRUE:
            return true;
        default:
            return false;
        }
	}

}
