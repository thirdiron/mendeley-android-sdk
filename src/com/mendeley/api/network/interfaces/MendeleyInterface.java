package com.mendeley.api.network.interfaces;

import com.mendeley.api.exceptions.MendeleyException;

public interface MendeleyInterface {

	public void onAPICallFail(MendeleyException exception);
}
