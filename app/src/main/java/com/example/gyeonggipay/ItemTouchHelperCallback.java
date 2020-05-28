//package com.example.gyeonggipay;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.ItemTouchHelper;
//import androidx.recyclerview.widget.RecyclerView;
//
//
////리뷰 남길때, 사진 리스트 나오는 화면에서
////스와프해서 리스트 자리 이동과 삭제시 사용할 콜백 클래스
//
//public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {
//
//    private ItemTouchHelperListener listener;
//    public ItemTouchHelperCallback(ItemTouchHelperListener listener) {
//        this.listener = listener;
//    }
//
//    //이동
//    //위-아래
//    //좌-우
//    @Override
//    public int getMovementFlags(@NonNull RecyclerView recyclerView,
//                                @NonNull RecyclerView.ViewHolder viewHolder) {
//        int drag_flags = ItemTouchHelper.UP|ItemTouchHelper.DOWN;
//        int swipe_flags = ItemTouchHelper.START|ItemTouchHelper.END;
//
//        //onSwipe만 사용(onMove 사용안함) - return makeMovementFlags(0,swipe_flags);
//        //return makeMovementFlags(drag_flags,swipe_flags);
//        return makeMovementFlags(0,swipe_flags);
//
//    }
//
//
//    @Override
//    public boolean isLongPressDragEnabled() {
//        return true;
//    }
//
//    @Override
//    public boolean onMove(@NonNull RecyclerView recyclerView,
//                          @NonNull RecyclerView.ViewHolder viewHolder,
//                          @NonNull RecyclerView.ViewHolder target) {
//        return listener.onItemMove(viewHolder.getAdapterPosition(),target.getAdapterPosition());
//    }
//
//    @Override public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//        listener.onItemSwipe(viewHolder.getAdapterPosition());
//    }
//
//}