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

package automata;

public enum AccType {
    
    BUCHI,
    RABIN,
    PARITY;
    
    public boolean isBuchi() {
        return this == BUCHI;
    }
    
    public boolean isRabin() {
        return this == RABIN;
    }
    
    public boolean isParity() {
        return this == PARITY;
    }
    
    
    @Override
    public String toString() {
        if(this == BUCHI) {
            return "NBA";
        }else if(this == RABIN) {
            return "DRA";
        }else {
            return "DPA";
        }
    }

}
