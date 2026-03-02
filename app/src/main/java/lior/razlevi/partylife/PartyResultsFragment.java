package lior.razlevi.partylife;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PartyResultsFragment extends Fragment {

    private RecyclerView rvPartyResults;
    private TextView tvNoResults;
    // כאן יהיה האדפטר שלך בהמשך
    // private PartyAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_party_results, container, false);
        
        rvPartyResults = view.findViewById(R.id.rvPartyResults);
        tvNoResults = view.findViewById(R.id.tvNoResults);
        
        rvPartyResults.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // כאן תטען את הנתונים ותעדכן את האדפטר
        // updateResults(new ArrayList<>());
        
        return view;
    }

    public void updateResults(List<Object> parties) {
        if (parties == null || parties.isEmpty()) {
            tvNoResults.setVisibility(View.VISIBLE);
            rvPartyResults.setVisibility(View.GONE);
        } else {
            tvNoResults.setVisibility(View.GONE);
            rvPartyResults.setVisibility(View.VISIBLE);
            // עדכון האדפטר עם הרשימה החדשה
        }
    }
}
