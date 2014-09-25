package com.code44.finance.ui.settings.data;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.code44.finance.R;
import com.code44.finance.ui.BaseFragment;
import com.code44.finance.ui.dialogs.ListDialogFragment;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class DataFragment extends BaseFragment implements View.OnClickListener {
    private static final int REQUEST_BACKUP_DESTINATION = 1;
    private static final int REQUEST_RESTORE_DESTINATION = 2;

    private static final String FRAGMENT_DESTINATION = "FRAGMENT_DESTINATION";

    private static final String ARG_EXPORT_TYPE = "ARG_EXPORT_TYPE";

    public static DataFragment newInstance() {
        return new DataFragment();
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_data, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get views
        final Button backup_B = (Button) view.findViewById(R.id.backup_B);
        final Button restore_B = (Button) view.findViewById(R.id.restore_B);

        // Setup
        backup_B.setOnClickListener(this);
        restore_B.setOnClickListener(this);
    }

    @Override public void onResume() {
        super.onResume();
        getEventBus().register(this);
    }

    @Override public void onPause() {
        super.onPause();
        getEventBus().unregister(this);
    }

    @Override public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backup_B:
                chooseBackup(REQUEST_BACKUP_DESTINATION, R.string.backup);
                break;
            case R.id.restore_B:
                chooseBackup(REQUEST_RESTORE_DESTINATION, R.string.restore);
                break;
        }
    }

    @Subscribe public void onBackupDestinationSelected(ListDialogFragment.ListDialogEvent event) {
        if ((event.getRequestCode() != REQUEST_BACKUP_DESTINATION && event.getRequestCode() != REQUEST_RESTORE_DESTINATION) || !event.isListItemClicked()) {
            return;
        }

        event.dismiss();

        if (event.getRequestCode() == REQUEST_BACKUP_DESTINATION) {
            final ExportActivity.Destination destination;
            if (event.getPosition() == 0) {
                destination = ExportActivity.Destination.GoogleDrive;
            } else {
                destination = ExportActivity.Destination.File;
            }

            ExportActivity.start(getActivity(), ExportActivity.ExportType.Backup, destination);
        } else {
            final ImportActivity.Source source;
            if (event.getPosition() == 0) {
                source = ImportActivity.Source.GoogleDrive;
            } else {
                source = ImportActivity.Source.File;
            }

            ImportActivity.start(getActivity(), ImportActivity.ImportType.Backup, source);
        }
    }

    private void chooseBackup(int requestCode, int titleResId) {
        final List<ListDialogFragment.ListDialogItem> items = new ArrayList<>();
        items.add(new ListDialogFragment.ListDialogItem(getString(R.string.google_drive)));
        items.add(new ListDialogFragment.ListDialogItem(getString(R.string.file)));

        final Bundle args = new Bundle();
        args.putSerializable(ARG_EXPORT_TYPE, ExportActivity.ExportType.Backup);

        ListDialogFragment.build(requestCode)
                .setTitle(getString(titleResId))
                .setArgs(args)
                .setNegativeButtonText(getString(R.string.cancel))
                .setItems(items)
                .build()
                .show(getChildFragmentManager(), FRAGMENT_DESTINATION);
    }
}