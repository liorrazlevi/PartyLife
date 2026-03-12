package lior.razlevi.partylife;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PartyResultsFragment extends Fragment implements PartyAdapter.OnPartyClickListener {

    private RecyclerView rvPartyResults;
    private TextView tvResultsCount;
    private LinearLayout llEmptyState;
    private PartyAdapter partyAdapter;
    private List<Party> partyList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_party_results, container, false);
        
        init(view);
        
        // אתחול רשימה ואדפטר
        partyList = new ArrayList<>();
        partyAdapter = new PartyAdapter(partyList, this);
        rvPartyResults.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPartyResults.setAdapter(partyAdapter);
        
        return view;
    }

    private void init(View view) {
        rvPartyResults = view.findViewById(R.id.rvPartyResults);
        tvResultsCount = view.findViewById(R.id.tvResultsCount);
        llEmptyState = view.findViewById(R.id.llEmptyState);
    }

    // פונקציה לעדכון התוצאות
    public void updateResults(List<Party> parties) {
        if (parties == null || parties.isEmpty()) {
            llEmptyState.setVisibility(View.VISIBLE);
            rvPartyResults.setVisibility(View.GONE);
            tvResultsCount.setText("לא נמצאו מסיבות");
        } else {
            llEmptyState.setVisibility(View.GONE);
            rvPartyResults.setVisibility(View.VISIBLE);
            tvResultsCount.setText("מצאנו " + parties.size() + " מסיבות שתואמות לחיפוש שלך");
            
            this.partyList.clear();
            this.partyList.addAll(parties);
            partyAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPartyClick(Party party) {
        // מעבר למסך פרטי מסיבה
        Intent intent = new Intent(getActivity(), party_details.class);
        intent.putExtra("PARTY_ID", party.getId());
        startActivity(intent);
    }
}
