package yy.tidialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiUIHelper;
import org.appcelerator.titanium.view.TiUIView;

import android.R;
//import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;

@Kroll.proxy(creatableInModule = TidialogsModule.class)
  public class MultiPickerProxy extends TiViewProxy {
    private class MultiPicker extends TiUIView {

      Builder builder;

      public MultiPicker(TiViewProxy proxy) {
        super(proxy);

      }

      private Builder getBuilder() {
        if (builder == null) {
          builder = new AlertDialog.Builder(this.proxy.getActivity());
          builder.setCancelable(true);
        }
        return builder;
      }

      @Override
      public void processProperties(KrollDict d) {
        super.processProperties(d);
        if (d.containsKey("title")) {
          getBuilder().setTitle(d.getString("title"));
        }
        if (d.containsKey("options")) {
          final String[] options = d.getStringArray("options");
          final ArrayList<Integer> mSelectedItems = new ArrayList<Integer>();

          boolean[] checked = new boolean[options.length];
          Arrays.fill(checked, Boolean.FALSE);
          if (d.containsKey("selected")) {
            List<String> s = Arrays.asList(d.getStringArray("selected"));
            for (int i = 0; i < options.length; i++) {
              checked[i] = s.contains(options[i]);
            }
          }
          getBuilder()
            .setMultiChoiceItems(options, checked, new DialogInterface.OnMultiChoiceClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                  mSelectedItems.add(which);
                } else if (mSelectedItems .contains(which)) {
                  mSelectedItems.remove(Integer.valueOf(which));
                }
              }
            })
          .setPositiveButton(R.string.ok,
              new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog,
                  int id) {
                  // convert to int array

                  ArrayList<String> selections = new ArrayList<String>();
                  for (Integer s: mSelectedItems) {
                    selections.add(options[s]);
                  }

                  KrollDict data = new KrollDict();
                  data.put("indexes", mSelectedItems .toArray(new Integer[mSelectedItems.size()]));
                  data.put("selections", selections.toArray(new String[selections.size()]));
                  fireEvent("click", data);
                }
              })
          .setNegativeButton(R.string.cancel,
              new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {

                }
              });
        }

      }

      public void show() {
        getBuilder().create().show();
        builder = null;
      }

    }

    public MultiPickerProxy() {
      super();
    }

    @Override
    public TiUIView createView(Activity activity) {
      return new MultiPicker(this);
    }

    @Override
    public void handleCreationDict(KrollDict options) {
      super.handleCreationDict(options);
    }

    @Override
    protected void handleShow(KrollDict options) {
      super.handleShow(options);
      TiUIHelper.runUiDelayedIfBlock(new Runnable() {
        @Override
        public void run() {
          MultiPicker d = (MultiPicker) getOrCreateView();
          d.show();
        }
      });
    }
  }
