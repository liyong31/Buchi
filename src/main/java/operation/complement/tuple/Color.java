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

package operation.complement.tuple;

public enum Color {
    
    NONE,
    ZERO,
    ONE,
    TWO,
    THREE;
    
    @Override
    public String toString() {
        if(this == NONE) {
            return "";
        }else if(this == ZERO) {
            return "" + 0;
        }else if(this == ONE) {
            return "" + 1;
        }else if(this == TWO) {
            return "" + 2;
        }else {
            return "" + 3;
        }
    }
}
