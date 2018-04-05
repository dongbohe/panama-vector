/*
 * Copyright (c) 2017, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have
 * questions.
 */
package jdk.incubator.vector;

import jdk.internal.vm.annotation.ForceInline;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("cast")
public abstract class ShortVector<S extends Vector.Shape> extends Vector<Short,S> {

    ShortVector() {}

    // Unary operator

    interface FUnOp {
        short apply(int i, short a);
    }

    abstract ShortVector<S> uOp(FUnOp f);

    abstract ShortVector<S> uOp(Mask<Short, S> m, FUnOp f);

    // Binary operator

    interface FBinOp {
        short apply(int i, short a, short b);
    }

    abstract ShortVector<S> bOp(Vector<Short,S> o, FBinOp f);

    abstract ShortVector<S> bOp(Vector<Short,S> o, Mask<Short, S> m, FBinOp f);

    // Trinary operator

    interface FTriOp {
        short apply(int i, short a, short b, short c);
    }

    abstract ShortVector<S> tOp(Vector<Short,S> o1, Vector<Short,S> o2, FTriOp f);

    abstract ShortVector<S> tOp(Vector<Short,S> o1, Vector<Short,S> o2, Mask<Short, S> m, FTriOp f);

    // Reduction operator

    abstract short rOp(short v, FBinOp f);

    // Binary test

    interface FBinTest {
        boolean apply(int i, short a, short b);
    }

    abstract Mask<Short, S> bTest(Vector<Short,S> o, FBinTest f);

    // Foreach

    interface FUnCon {
        void apply(int i, short a);
    }

    abstract void forEach(FUnCon f);

    abstract void forEach(Mask<Short, S> m, FUnCon f);

    //

    @Override
    public ShortVector<S> add(Vector<Short,S> o) {
        return bOp(o, (i, a, b) -> (short) (a + b));
    }

    /**
     * Adds this vector to the result of broadcasting an input scalar.
     * <p>
     * This is a vector binary operation where the primitive addition operation
     * ({@code +}) is applied to lane elements.
     *
     * @param b the input scalar
     * @return the result of adding this vector to the broadcast of an input
     * scalar
     */
    public abstract ShortVector<S> add(short b);

    @Override
    public ShortVector<S> add(Vector<Short,S> o, Mask<Short, S> m) {
        return bOp(o, m, (i, a, b) -> (short) (a + b));
    }

    /**
     * Adds this vector to the result of broadcasting an input scalar,
     * selecting lane elements controlled by a mask.
     * <p>
     * This is a vector binary operation where the primitive addition operation
     * ({@code +}) is applied to lane elements.
     *
     * @param b the input vector
     * @param m the mask controlling lane selection
     * @return the result of adding this vector to the broadcast of an input
     * scalar
     */
    public abstract ShortVector<S> add(short b, Mask<Short, S> m);

    @Override
    public ShortVector<S> addSaturate(Vector<Short,S> o) {
        return bOp(o, (i, a, b) -> (short) ((a >= Integer.MAX_VALUE || Integer.MAX_VALUE - b > a) ? Integer.MAX_VALUE : a + b));
    }

    public abstract ShortVector<S> addSaturate(short o);

    @Override
    public ShortVector<S> addSaturate(Vector<Short,S> o, Mask<Short, S> m) {
        return bOp(o, m, (i, a, b) -> (short) ((a >= Integer.MAX_VALUE || Integer.MAX_VALUE - b > a) ? Integer.MAX_VALUE : a + b));
    }

    public abstract ShortVector<S> addSaturate(short o, Mask<Short, S> m);

    @Override
    public ShortVector<S> sub(Vector<Short,S> o) {
        return bOp(o, (i, a, b) -> (short) (a - b));
    }

    public abstract ShortVector<S> sub(short o);

    @Override
    public ShortVector<S> sub(Vector<Short,S> o, Mask<Short, S> m) {
        return bOp(o, m, (i, a, b) -> (short) (a - b));
    }

    public abstract ShortVector<S> sub(short o, Mask<Short, S> m);

    @Override
    public ShortVector<S> subSaturate(Vector<Short,S> o) {
        return bOp(o, (i, a, b) -> (short) ((a >= Integer.MIN_VALUE || Integer.MIN_VALUE + b > a) ? Integer.MAX_VALUE : a - b));
    }

    public abstract ShortVector<S> subSaturate(short o);

    @Override
    public ShortVector<S> subSaturate(Vector<Short,S> o, Mask<Short, S> m) {
        return bOp(o, m, (i, a, b) -> (short) ((a >= Integer.MIN_VALUE || Integer.MIN_VALUE + b > a) ? Integer.MAX_VALUE : a - b));
    }

    public abstract ShortVector<S> subSaturate(short o, Mask<Short, S> m);

    @Override
    public ShortVector<S> mul(Vector<Short,S> o) {
        return bOp(o, (i, a, b) -> (short) (a * b));
    }

    public abstract ShortVector<S> mul(short o);

    @Override
    public ShortVector<S> mul(Vector<Short,S> o, Mask<Short, S> m) {
        return bOp(o, m, (i, a, b) -> (short) (a * b));
    }

    public abstract ShortVector<S> mul(short o, Mask<Short, S> m);

    @Override
    public ShortVector<S> neg() {
        return uOp((i, a) -> (short) (-a));
    }

    @Override
    public ShortVector<S> neg(Mask<Short, S> m) {
        return uOp(m, (i, a) -> (short) (-a));
    }

    @Override
    public ShortVector<S> abs() {
        return uOp((i, a) -> (short) Math.abs(a));
    }

    @Override
    public ShortVector<S> abs(Mask<Short, S> m) {
        return uOp(m, (i, a) -> (short) Math.abs(a));
    }

    @Override
    public ShortVector<S> min(Vector<Short,S> o) {
        return bOp(o, (i, a, b) -> (a <= b) ? a : b);
    }

    public abstract ShortVector<S> min(short o);

    @Override
    public ShortVector<S> max(Vector<Short,S> o) {
        return bOp(o, (i, a, b) -> (a >= b) ? a : b);
    }

    public abstract ShortVector<S> max(short o);

    @Override
    public Mask<Short, S> equal(Vector<Short,S> o) {
        return bTest(o, (i, a, b) -> a == b);
    }

    public abstract Mask<Short, S> equal(short o);

    @Override
    public Mask<Short, S> notEqual(Vector<Short,S> o) {
        return bTest(o, (i, a, b) -> a != b);
    }

    public abstract Mask<Short, S> notEqual(short o);

    @Override
    public Mask<Short, S> lessThan(Vector<Short,S> o) {
        return bTest(o, (i, a, b) -> a < b);
    }

    public abstract Mask<Short, S> lessThan(short o);

    @Override
    public Mask<Short, S> lessThanEq(Vector<Short,S> o) {
        return bTest(o, (i, a, b) -> a <= b);
    }

    public abstract Mask<Short, S> lessThanEq(short o);

    @Override
    public Mask<Short, S> greaterThan(Vector<Short,S> o) {
        return bTest(o, (i, a, b) -> a > b);
    }

    public abstract Mask<Short, S> greaterThan(short o);

    @Override
    public Mask<Short, S> greaterThanEq(Vector<Short,S> o) {
        return bTest(o, (i, a, b) -> a >= b);
    }

    public abstract Mask<Short, S> greaterThanEq(short o);

    @Override
    public ShortVector<S> blend(Vector<Short,S> o, Mask<Short, S> m) {
        return bOp(o, (i, a, b) -> m.getElement(i) ? b : a);
    }

    public abstract ShortVector<S> blend(short o, Mask<Short, S> m);

    @Override
    public abstract ShortVector<S> shuffle(Vector<Short,S> o, Shuffle<Short, S> m);

    @Override
    public abstract ShortVector<S> swizzle(Shuffle<Short, S> m);

    @Override
    @ForceInline
    public <T extends Shape> ShortVector<T> resize(Species<Short, T> species) {
        return (ShortVector<T>) species.reshape(this);
    }

    @Override
    public abstract ShortVector<S> rotateEL(int i);

    @Override
    public abstract ShortVector<S> rotateER(int i);

    @Override
    public abstract ShortVector<S> shiftEL(int i);

    @Override
    public abstract ShortVector<S> shiftER(int i);


    public ShortVector<S> and(Vector<Short,S> o) {
        return bOp(o, (i, a, b) -> (short) (a & b));
    }

    public abstract ShortVector<S> and(short o);

    public ShortVector<S> and(Vector<Short,S> o, Mask<Short, S> m) {
        return bOp(o, m, (i, a, b) -> (short) (a & b));
    }

    public abstract ShortVector<S> and(short o, Mask<Short, S> m);

    public ShortVector<S> or(Vector<Short,S> o) {
        return bOp(o, (i, a, b) -> (short) (a | b));
    }

    public abstract ShortVector<S> or(short o);

    public ShortVector<S> or(Vector<Short,S> o, Mask<Short, S> m) {
        return bOp(o, m, (i, a, b) -> (short) (a | b));
    }

    public abstract ShortVector<S> or(short o, Mask<Short, S> m);

    public ShortVector<S> xor(Vector<Short,S> o) {
        return bOp(o, (i, a, b) -> (short) (a ^ b));
    }

    public abstract ShortVector<S> xor(short o);

    public ShortVector<S> xor(Vector<Short,S> o, Mask<Short, S> m) {
        return bOp(o, m, (i, a, b) -> (short) (a ^ b));
    }

    public abstract ShortVector<S> xor(short o, Mask<Short, S> m);

    public ShortVector<S> not() {
        return uOp((i, a) -> (short) (~a));
    }

    public ShortVector<S> not(Mask<Short, S> m) {
        return uOp(m, (i, a) -> (short) (~a));
    }

    // logical shift left
    public ShortVector<S> shiftL(Vector<Short,S> o) {
        return bOp(o, (i, a, b) -> (short) (a << b));
    }

    public ShortVector<S> shiftL(int s) {
        return uOp((i, a) -> (short) (a << s));
    }

    public ShortVector<S> shiftL(Vector<Short,S> o, Mask<Short, S> m) {
        return bOp(o, m, (i, a, b) -> (short) (a << b));
    }

    public ShortVector<S> shiftL(int s, Mask<Short, S> m) {
        return uOp(m, (i, a) -> (short) (a << s));
    }

    // logical, or unsigned, shift right
    public ShortVector<S> shiftR(Vector<Short,S> o) {
        return bOp(o, (i, a, b) -> (short) (a >>> b));
    }

    public ShortVector<S> shiftR(int s) {
        return uOp((i, a) -> (short) (a >>> s));
    }

    public ShortVector<S> shiftR(Vector<Short,S> o, Mask<Short, S> m) {
        return bOp(o, m, (i, a, b) -> (short) (a >>> b));
    }

    public ShortVector<S> shiftR(int s, Mask<Short, S> m) {
        return uOp(m, (i, a) -> (short) (a >>> s));
    }

    // arithmetic, or signed, shift right
    public ShortVector<S> ashiftR(Vector<Short,S> o) {
        return bOp(o, (i, a, b) -> (short) (a >> b));
    }

    public ShortVector<S> aShiftR(int s) {
        return uOp((i, a) -> (short) (a >> s));
    }

    public ShortVector<S> ashiftR(Vector<Short,S> o, Mask<Short, S> m) {
        return bOp(o, m, (i, a, b) -> (short) (a >> b));
    }

    public ShortVector<S> aShiftR(int s, Mask<Short, S> m) {
        return uOp(m, (i, a) -> (short) (a >> s));
    }

    public ShortVector<S> rotateL(int j) {
        return uOp((i, a) -> (short) Integer.rotateLeft(a, j));
    }

    public ShortVector<S> rotateR(int j) {
        return uOp((i, a) -> (short) Integer.rotateRight(a, j));
    }

    @Override
    public void intoByteArray(byte[] a, int ix) {
        ByteBuffer bb = ByteBuffer.wrap(a, ix, a.length - ix);
        intoByteBuffer(bb);
    }

    @Override
    public void intoByteArray(byte[] a, int ix, Mask<Short, S> m) {
        ByteBuffer bb = ByteBuffer.wrap(a, ix, a.length - ix);
        intoByteBuffer(bb, m);
    }

    @Override
    public void intoByteBuffer(ByteBuffer bb) {
        ShortBuffer fb = bb.asShortBuffer();
        forEach((i, a) -> fb.put(a));
    }

    @Override
    public void intoByteBuffer(ByteBuffer bb, Mask<Short, S> m) {
        ShortBuffer fb = bb.asShortBuffer();
        forEach((i, a) -> {
            if (m.getElement(i))
                fb.put(a);
            else
                fb.position(fb.position() + 1);
        });
    }

    @Override
    public void intoByteBuffer(ByteBuffer bb, int ix) {
        bb = bb.duplicate().position(ix);
        ShortBuffer fb = bb.asShortBuffer();
        forEach((i, a) -> fb.put(i, a));
    }

    @Override
    public void intoByteBuffer(ByteBuffer bb, int ix, Mask<Short, S> m) {
        bb = bb.duplicate().position(ix);
        ShortBuffer fb = bb.asShortBuffer();
        forEach(m, (i, a) -> fb.put(i, a));
    }


    // Type specific horizontal reductions

    /**
     * Sums all lane elements of this vector.
     * <p>
     * This is an associative vector reduction operation where the addition
     * operation ({@code +}) is applied to lane elements, and the identity value
     * is {@code 0}.
     *
     * @return the sum of all the lane elements of this vector
     */
    public short addAll() {
        return rOp((short) 0, (i, a, b) -> (short) (a + b));
    }

    public short subAll() {
        return rOp((short) 0, (i, a, b) -> (short) (a - b));
    }

    public short mulAll() {
        return rOp((short) 1, (i, a, b) -> (short) (a * b));
    }

    public short minAll() {
        return rOp(Short.MAX_VALUE, (i, a, b) -> a > b ? b : a);
    }

    public short maxAll() {
        return rOp(Short.MIN_VALUE, (i, a, b) -> a < b ? b : a);
    }

    public short orAll() {
        return rOp((short) 0, (i, a, b) -> (short) (a | b));
    }

    public short andAll() {
        return rOp((short) -1, (i, a, b) -> (short) (a & b));
    }

    public short xorAll() {
        return rOp((short) 0, (i, a, b) -> (short) (a ^ b));
    }

    // Type specific accessors

    /**
     * Gets the lane element at lane index {@code i}
     *
     * @param i the lane index
     * @return the lane element at lane index {@code i}
     */
    public abstract short get(int i);

    /**
     * Replaces the lane element of this vector at lane index {@code i} with
     * value {@code e}.
     * <p>
     * This is a cross-lane operation.
     * @@@ specify as blend(species().broadcast(e), mask)
     *
     * @param i the lane index of the lane element to be replaced
     * @param e the value to be placed
     * @return the result of replacing the lane element of this vector at lane
     * index {@code i} with value {@code e}.
     */
    public abstract ShortVector<S> with(int i, short e);

    // Type specific extractors

    @ForceInline
    public short[] toArray() {
        short[] a = new short[species().length()];
        intoArray(a, 0);
        return a;
    }

    public void intoArray(short[] a, int ax) {
        forEach((i, a_) -> a[ax + i] = a_);
    }

    public void intoArray(short[] a, int ax, Mask<Short, S> m) {
        forEach(m, (i, a_) -> a[ax + i] = a_);
    }

    public void intoArray(short[] a, int ax, int[] indexMap, int mx) {
        forEach((i, a_) -> a[ax + indexMap[mx + i]] = a_);
    }

    public void intoArray(short[] a, int ax, Mask<Short, S> m, int[] indexMap, int mx) {
        forEach(m, (i, a_) -> a[ax + indexMap[mx + i]] = a_);
    }

    // Species

    @Override
    public abstract ShortSpecies<S> species();

    public static abstract class ShortSpecies<S extends Vector.Shape> extends Vector.Species<Short, S> {
        interface FOp {
            short apply(int i);
        }

        abstract ShortVector<S> op(FOp f);

        abstract ShortVector<S> op(Mask<Short, S> m, FOp f);

        // Factories

        @Override
        public ShortVector<S> zero() {
            return op(i -> 0);
        }

        public ShortVector<S> broadcast(short e) {
            return op(i -> e);
        }

        public ShortVector<S> single(short e) {
            return op(i -> i == 0 ? e : (short) 0);
        }

        public ShortVector<S> random() {
            ThreadLocalRandom r = ThreadLocalRandom.current();
            return op(i -> (short) r.nextInt());
        }

        public ShortVector<S> scalars(short... es) {
            return op(i -> es[i]);
        }

        /**
         * Loads a vector from an array starting at offset.
         * <p>
         * For each vector lane, where {@code N} is the vector lane index, the
         * array element at index {@code i + N} is placed into the
         * resulting vector at lane index {@code N}.
         *
         * @param a the array
         * @param i the offset into the array
         * @return the vector loaded from an array
         * @throws IndexOutOfBoundsException if {@code i < 0} or
         * {@code i < a.length - this.length()}
         */
        public ShortVector<S> fromArray(short[] a, int i) {
            return op(n -> a[i + n]);
        }

        /**
         * Loads a vector from an array starting at offset.
         * <p>
         * For each vector lane, where {@code N} is the vector lane index,
         * if the mask lane at index {@code N} is set then the array element at
         * index {@code i + N} is placed into the resulting vector at lane index
         * {@code N}, otherwise the default element value is placed into the
         * resulting vector at lane index {@code N}.
         *
         * @param a the array
         * @param i the offset into the array
         * @param m the mask
         * @return the vector loaded from an array
         * @throws IndexOutOfBoundsException if {@code i < 0} or
         * {@code i < a.length - this.length()}
         */
        public ShortVector<S> fromArray(short[] a, int i, Mask<Short, S> m) {
            return op(m, n -> a[i + n]);
        }

        /**
         * Loads a vector from an array using indexes obtained from an index
         * map.
         * <p>
         * For each vector lane, where {@code N} is the vector lane index, the
         * array element at index {@code i + indexMap[j + N]} is placed into the
         * resulting vector at lane index {@code N}.
         *
         * @param a the array
         * @param i the offset into the array, may be negative if relative
         * indexes in the index map compensate to produce a value within the
         * array bounds
         * @param indexMap the index map
         * @param j the offset into the index map
         * @return the vector loaded from an array
         * @throws IndexOutOfBoundsException if {@code j < 0}, or
         * {@code j < indexMap.length - this.length()}, or for any vector
         * lane index {@code N} the result of {@code i + indexMap[j + N]} is
         * {@code < 0} or {@code >= a.length}
         */
        public ShortVector<S> fromArray(short[] a, int i, int[] indexMap, int j) {
            return op(n -> a[i + indexMap[j + n]]);
        }

        /**
         * Loads a vector from an array using indexes obtained from an index
         * map.
         * <p>
         * For each vector lane, where {@code N} is the vector lane index,
         * if the mask lane at index {@code N} is set then the array element at
         * index {@code i + indexMap[j + N]} is placed into the resulting vector
         * at lane index {@code N}, otherwise the default element value is
         * placed into the resulting vector at lane index {@code N}.
         *
         * @param a the array
         * @param i the offset into the array, may be negative if relative
         * indexes in the index map compensate to produce a value within the
         * array bounds
         * @param m the mask
         * @param indexMap the index map
         * @param j the offset into the index map
         * @return the vector loaded from an array
         * @throws IndexOutOfBoundsException if {@code j < 0}, or
         * {@code j < indexMap.length - this.length()}, or for any vector
         * lane index {@code N} the result of {@code i + indexMap[j + N]} is
         * {@code < 0} or {@code >= a.length}
         */
        public ShortVector<S> fromArray(short[] a, int i, Mask<Short, S> m, int[] indexMap, int j) {
            return op(m, n -> a[i + indexMap[j + n]]);
        }

        @Override
        public ShortVector<S> fromByteArray(byte[] a, int ix) {
            ByteBuffer bb = ByteBuffer.wrap(a, ix, a.length - ix);
            return fromByteBuffer(bb);
        }

        @Override
        public ShortVector<S> fromByteArray(byte[] a, int ix, Mask<Short, S> m) {
            ByteBuffer bb = ByteBuffer.wrap(a, ix, a.length - ix);
            return fromByteBuffer(bb, m);
        }

        @Override
        public ShortVector<S> fromByteBuffer(ByteBuffer bb) {
            ShortBuffer fb = bb.asShortBuffer();
            return op(i -> fb.get());
        }

        @Override
        public ShortVector<S> fromByteBuffer(ByteBuffer bb, Mask<Short, S> m) {
            ShortBuffer fb = bb.asShortBuffer();
            return op(i -> {
                if(m.getElement(i))
                    return fb.get();
                else {
                    fb.position(fb.position() + 1);
                    return (short) 0;
                }
            });
        }

        @Override
        public ShortVector<S> fromByteBuffer(ByteBuffer bb, int ix) {
            bb = bb.duplicate().position(ix);
            ShortBuffer fb = bb.asShortBuffer();
            return op(i -> fb.get(i));
        }

        @Override
        public ShortVector<S> fromByteBuffer(ByteBuffer bb, int ix, Mask<Short, S> m) {
            bb = bb.duplicate().position(ix);
            ShortBuffer fb = bb.asShortBuffer();
            return op(m, i -> fb.get(i));
        }

        @Override
        public <F, T extends Shape> ShortVector<S> reshape(Vector<F, T> o) {
            int blen = Math.max(o.species().bitSize(), bitSize()) / Byte.SIZE;
            ByteBuffer bb = ByteBuffer.allocate(blen).order(ByteOrder.nativeOrder());
            o.intoByteBuffer(bb, 0);
            return fromByteBuffer(bb, 0);
        }

        @Override
        @ForceInline
        public <F> ShortVector<S> rebracket(Vector<F, S> o) {
            return reshape(o);
        }

        @Override
        @ForceInline
        public <T extends Shape> ShortVector<S> resize(Vector<Short, T> o) {
            return reshape(o);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <F, T extends Shape> ShortVector<S> cast(Vector<F, T> v) {
            // Allocate array of required size
            short[] a = new short[length()];

            Class<?> vtype = v.species().elementType();
            int limit = Math.min(v.species().length(), length());
            if (vtype == Byte.class) {
                ByteVector<T> tv = (ByteVector<T>)v;
                for (int i = 0; i < limit; i++) {
                    a[i] = (short) tv.get(i);
                }
            } else if (vtype == Short.class) {
                ShortVector<T> tv = (ShortVector<T>)v;
                for (int i = 0; i < limit; i++) {
                    a[i] = (short) tv.get(i);
                }
            } else if (vtype == Integer.class) {
                IntVector<T> tv = (IntVector<T>)v;
                for (int i = 0; i < limit; i++) {
                    a[i] = (short) tv.get(i);
                }
            } else if (vtype == Long.class){
                LongVector<T> tv = (LongVector<T>)v;
                for (int i = 0; i < limit; i++) {
                    a[i] = (short) tv.get(i);
                }
            } else if (vtype == Float.class){
                FloatVector<T> tv = (FloatVector<T>)v;
                for (int i = 0; i < limit; i++) {
                    a[i] = (short) tv.get(i);
                }
            } else if (vtype == Double.class){
                DoubleVector<T> tv = (DoubleVector<T>)v;
                for (int i = 0; i < limit; i++) {
                    a[i] = (short) tv.get(i);
                }
            } else {
                throw new UnsupportedOperationException("Bad lane type for casting.");
            }

            return scalars(a);
        }

    }
}
