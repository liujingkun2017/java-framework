package org.liujk.java.framework.base.utils.lang.beans.collection;


import java.util.EmptyStackException;
import java.util.Iterator;

/**
 * 说明：
 * <p>
 * Stack 接口表示后进先出（LIFO）的对象堆栈。它提供了通常的 push 和 pop 操作，以及取栈顶点的 peek 方法、测试堆栈是否为空的 isEmpty
 * 方法、在堆栈中查找项并确定到栈顶距离的 search 方法,并且迭代器从栈顶开始迭代元素。
 * <p>
 * 它与标准库 中的 {@link java.util.Stack} 相比，没有破坏栈结构的方法出现。
 *
 */
public interface Stack<E> extends Iterable<E> {

    /**
     * 测试堆栈是否为空。
     *
     * @return 当且仅当堆栈中不含任何项时返回 true；否则返回 false。
     */
    boolean isEmpty();

    /**
     * 查看栈顶对象而不移除它。
     *
     * @return 栈顶对象。
     * @throws EmptyStackException 如果堆栈是空的。
     */
    E peek();

    /**
     * 移除栈顶对象并作为此函数的值返回该对象(弹出)。
     *
     * @return 栈顶对象。
     * @throws EmptyStackException 如果堆栈是空的。
     */
    E pop();

    /**
     * 把项压入栈顶(推入)。
     *
     * @param item 压入栈的项。
     *
     * @return item 参数。
     */
    E push(E item);

    /**
     * 返回对象在栈中的位置，以 1 为基数。如果对象 item 是栈中的一个项，该方法返回距栈顶最近的出现位置到栈顶的距离；栈中最上端项的距离为 1。使用 item 的 equals 方法比较
     * item 与堆栈中的项。
     *
     * @param item 目标对象。
     *
     * @return 对象到栈顶的位置，以 1 为基数；返回值 -1 表示此对象不在堆栈中。
     */
    int search(E item);

    /**
     * 测试指定的对象是否为此栈中的 项。
     *
     * @param item 一个对象。
     *
     * @return 当且仅当指定的对象与此此栈中的某个项相同时（由 equals 方法确定）返回 true；否则返回 false。
     */
    boolean contains(E item);

    /**
     * 清空该栈。
     */
    void clear();

    /**
     * 返回此栈的高度(也是栈中的项数)。
     *
     * @return 栈的高度。
     */
    int height();

    /**
     * 返回该栈的迭代器，该迭代器从栈顶开始迭代元素。
     *
     * @return 该栈的迭代器。
     */
    @Override
    Iterator<E> iterator();

    /**
     * 返回包含此 Stack 中所有元素的数组，该数组的元素和迭代器返回的元素顺序相同。
     * <p>
     * 返回的数组将是“安全的”，因为此 Stack 并不维护对返回数组的任何引用。（换句话说，即使 Stack
     * 受到数组的支持，此方法也必须分配一个新的数组）。因此，调用方可以自由地修改返回的数组。
     * <p>
     * 此方法充当了基于数组的 API 与基于 Stack 的 API 之间的桥梁。
     *
     * @return 包含此 Stack 中所有元素的数组。
     * @see Stack#iterator()
     */
    Object[] toArray();

    /**
     * 返回包含此 Stack 中所有元素的数组，该数组的元素和迭代器返回的元素顺序相同；返回数组的运行时类型与指定数组的运行时类型相同。
     * <p>
     * 如果指定的数组能容纳该 Stack，则返回包含此 Stack 元素的数组。否则，将根据指定数组的运行时类型和此 Stack 的大小分配一个新数组。
     * <p>
     * <p>
     * 如果指定的数组能容纳 Stack 并有剩余空间（即数组的元素比 Stack 的元素多），那么会将数组中紧跟在 Stack 末尾的元素设置为 null。（这对确定 Stack
     * 的长度很有用，但只有 在调用方知道此 Stack 没有包含任何 null 元素时才可行。）
     * <p>
     * <p>
     * 像 {@link #toArray()} 方法一样，此方法充当了基于数组的 API 与基于 Stack 的 API
     * 之间的桥梁。更进一步说，此方法允许在输出数组的运行时类型上进行精确控制，并且在某些情况下，可以用来节省分配开销。
     *
     * @param array 存储此 Stack 元素的数组（如果其足够大）；否则，将为此分配一个具有相同运行时类型的新数组。
     *
     * @return 包含此 Stack 元素的数组。
     * @see Stack#iterator()
     */
    E[] toArray(E[] array);

    /**
     * 返回此 Stack 的字符串表示形式。该字符串表示形式由 Stack 元素的列表组成，这些元素按其迭代器返回的顺序排列，并用方括号 ("[]") 括起来。相邻元素由字符 ",
     * "（逗号加空格）分隔。通过 String.valueOf(Object) 可以将元素转换成字符串。 此实现会创建一个空字符串缓冲区，追加一个左方括号，然后在 Stack
     * 上进行迭代，依次追加每个元素的字符串表示形式。每追加一个元素后（最后一个元素除外），将追加字符串 ", "。最后，追加一个右括号。从字符串缓冲区获取一个字符串并返回它。
     *
     * @return 此 Stack 的字符串表示形式。
     */
    @Override
    String toString();
}
