package mcheli.multithread;

import mcheli.MCH_ClientProxy;
import mcheli.helicopter.MCH_HeliInfoManager;
import mcheli.plane.MCP_PlaneInfoManager;
import mcheli.tank.MCH_TankInfoManager;
import mcheli.vehicle.MCH_VehicleInfoManager;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class MultiThreadModelManager {


    /**
     * <p> Starts the MultiThreaded model loading process. </p>
     * Called from {@link MCH_ClientProxy#registerModels()}. Essentially loads the models in the background.
     * Since this can be done at runtime, there's no issue with this.
     * The load is balanced during loading generally and into the first seconds into login.
     * As of now, there is no way to tell whether the process is done other than to check the log for the 5 prints.
     *
     * @param proxy The unique ClientProxy object that called this.
     */

    public static void start(MCH_ClientProxy proxy) {

        ExecutorService executor = Executors.newWorkStealingPool();

        CompletableFuture
                .runAsync(() -> MCH_HeliInfoManager.map.keySet().forEach(key -> proxy.registerModelsHeli((String) key, false)), executor)
                .thenRun(() -> completion("helicopter"));

        CompletableFuture
                .runAsync(() -> MCP_PlaneInfoManager.map.keySet().forEach(key -> proxy.registerModelsPlane((String) key, false)), executor)
                .thenRun(() -> completion("plane"));

        CompletableFuture
                .runAsync(() -> MCH_TankInfoManager.map.keySet().forEach(key -> proxy.registerModelsTank((String) key, false)), executor)
                .thenRun(() -> completion("tank"));

        CompletableFuture
                .runAsync(() -> MCH_VehicleInfoManager.map.keySet().forEach(key -> proxy.registerModelsVehicle((String) key, false)), executor)
                .thenRun(() -> completion("vehicle"));

        CompletableFuture
                .runAsync(MCH_ClientProxy::registerModels_Bullet)
                .thenRun(() -> completion("bullet"));

        CompletableFuture
                .runAsync(MCH_ClientProxy::registerModels_Throwable)
                .thenRun(() -> completion("throwable"));

        executor.shutdown();

    }

    public static void completion(String type) {
        System.out.println("Successfully registered " + type + " models");
    }

}