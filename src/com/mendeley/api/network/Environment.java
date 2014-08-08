package com.mendeley.api.network;

import java.util.concurrent.Executor;

public interface Environment {
    public Executor getExecutor();
}
