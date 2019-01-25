package org.alertpreparedness.platform.v1.responseplan;

/**
 * Created by Tj on 12/12/2017.
 */

public class ArchivedFragment extends ActiveFragment{

    public ArchivedFragment() {
        active = false;
    }

    @Override
    protected ResponsePlansAdapter getmAdapter() {
        return new ResponsePlansAdapter(getContext(), responsePlans, false, this);
    }

//    @Override
//    public void onDataChange(DataSnapshot dataSnapshot) {
//        for(DataSnapshot child : dataSnapshot.getChildren()) {
//            if(child.child("isActive").exists()) {
//
//                boolean isActive = (boolean) child.child("isActive").getValue();
//
//                if (!isActive) {
//                    int regionalApproval = getApprovalStatus(child.child("approval").child("regionDirector"));
//                    int countryApproval = getApprovalStatus(child.child("approval").child("countryDirector"));
//                    int globalApproval = getApprovalStatus(child.child("approval").child("globalDirector"));
//
//                    Long createdAt = (Long) child.child("timeCreated").getValue();
//                    String hazardType = ExtensionHelperKt.getHazardTypes().get(Integer.valueOf((String) child.child("hazardScenario").getValue()));
//                    String percentCompleted = String.valueOf(child.child("sectionsCompleted").getValue());
//                    int status = Integer.valueOf(String.valueOf(child.child("status").getValue()));
//                    String name = (String) child.child("name").getValue();
//
//                    System.out.println("child.getId() = " + child.getId());
//                    System.out.println("user.countryID = " + user.countryID);
//
//                    mAdapter.addItem(child.getId(), new ResponsePlanObj(
//                            hazardType,
//                            percentCompleted,
//                            name,
//                            status,
//                            new Date(createdAt),
//                            regionalApproval,
//                            countryApproval,
//                            globalApproval)
//                    );
//                }
//            }
//        }
//    }


}
