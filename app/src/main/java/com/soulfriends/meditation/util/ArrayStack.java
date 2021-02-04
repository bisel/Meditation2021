package com.soulfriends.meditation.util;

public class ArrayStack {

    private int top;
    private int maxSize;
    private Object[] stackArray;

    // 배열 스택 생성,  스택의 최대 크기로 생성
    public ArrayStack(int maxSize){

        this.maxSize = maxSize;
        this.stackArray = new Object[maxSize];
        this.top = -1;
    }

    // 스택이 비어있는지 체크
    public boolean empty(){
        return (top == -1);
    }

    // 스택이 꽉찼는지 체크
    public boolean full(){
        return (top == maxSize-1);
    }

    // 스택에 item 입력
    public void push(Object item){

        if(full()) throw new ArrayIndexOutOfBoundsException((top+1)+">=" + maxSize);

        stackArray[++top] = item;
    }

    // 스택의 가장 위의 데이터 반환
    public Object peek(){

        if(empty())
        {
            return null;
        }

        return stackArray[top];
    }

    // 스택의 가장 위의 데이터 제거
    public Object pop(){

        Object item = peek();

        top--;

        return item;
    }

    public void Clear()
    {
        while(!empty())
        {
            pop();
        }

        int xx = 0;
    }

}
