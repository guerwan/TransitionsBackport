/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.support.transition;

import android.support.transition.utils.ArrayMap;
import android.support.transition.utils.OverlayCompatibilityHelper;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * This class manages the set of transitions that fire when there is a
 * change of {@link android.support.transition.Scene}. To use the manager, add scenes along with
 * transition objects with calls to {@link #setTransition(android.support.transition.Scene, android.support.transition.Transition)}
 * or {@link #setTransition(android.support.transition.Scene, android.support.transition.Scene, android.support.transition.Transition)}. Setting specific
 * transitions for scene changes is not required; by default, a Scene change
 * will use {@link android.support.transition.AutoTransition} to do something reasonable for most
 * situations. Specifying other transitions for particular scene changes is
 * only necessary if the application wants different transition behavior
 * in these situations.
 *
 * <p>TransitionManagers can be declared in XML resource files inside the
 * <code>res/transition</code> directory. TransitionManager resources consist of
 * the <code>transitionManager</code>tag name, containing one or more
 * <code>transition</code> tags, each of which describe the relationship of
 * that transition to the from/to scene information in that tag.
 * For example, here is a resource file that declares several scene
 * transitions:</p>
 *
 * {@sample development/samples/ApiDemos/res/transition/transitions_mgr.xml TransitionManager}
 *
 * <p>For each of the <code>fromScene</code> and <code>toScene</code> attributes,
 * there is a reference to a standard XML layout file. This is equivalent to
 * creating a scene from a layout in code by calling
 * {@link android.support.transition.Scene#getSceneForLayout(android.view.ViewGroup, int, android.content.Context)}. For the
 * <code>transition</code> attribute, there is a reference to a resource
 * file in the <code>res/transition</code> directory which describes that
 * transition.</p>
 *
 * Information on XML resource descriptions for transitions can be found for
 * {@link com.guerwan.transitionsbackport.R.styleable#Transition}, {@link com.guerwan.transitionsbackport.R.styleable#TransitionSet},
 * {@link com.guerwan.transitionsbackport.R.styleable#TransitionTarget}, {@link com.guerwan.transitionsbackport.R.styleable#Fade},
 * and {@link com.guerwan.transitionsbackport.R.styleable#TransitionManager}.
 */
public class TransitionManager {
    // TODO: how to handle enter/exit?

    private static String LOG_TAG = "TransitionManager";

    private static android.support.transition.Transition sDefaultTransition = new android.support.transition.AutoTransition();

    ArrayMap<Scene, Transition> mSceneTransitions = new ArrayMap<android.support.transition.Scene, android.support.transition.Transition>();
    ArrayMap<android.support.transition.Scene, ArrayMap<android.support.transition.Scene, android.support.transition.Transition>> mScenePairTransitions =
            new ArrayMap<android.support.transition.Scene, ArrayMap<android.support.transition.Scene, android.support.transition.Transition>>();
    private static ThreadLocal<WeakReference<ArrayMap<ViewGroup, ArrayList<android.support.transition.Transition>>>>
            sRunningTransitions =
            new ThreadLocal<WeakReference<ArrayMap<ViewGroup, ArrayList<android.support.transition.Transition>>>>();
    private static ArrayList<ViewGroup> sPendingTransitions = new ArrayList<ViewGroup>();


    /**
     * Sets the transition to be used for any scene change for which no
     * other transition is explicitly set. The initial value is
     * an {@link android.support.transition.AutoTransition} instance.
     *
     * @param transition The default transition to be used for scene changes.
     */
    public void setDefaultTransition(android.support.transition.Transition transition) {
        sDefaultTransition = transition;
    }

    /**
     * Gets the current default transition. The initial value is an {@link
     * android.support.transition.AutoTransition} instance.
     *
     * @return The current default transition.
     * @see #setDefaultTransition(android.support.transition.Transition)
     */
    public static android.support.transition.Transition getDefaultTransition() {
        return sDefaultTransition;
    }

    /**
     * Sets a specific transition to occur when the given scene is entered.
     *
     * @param scene The scene which, when applied, will cause the given
     * transition to run.
     * @param transition The transition that will play when the given scene is
     * entered. A value of null will result in the default behavior of
     * using the {@link #getDefaultTransition() default transition} instead.
     */
    public void setTransition(android.support.transition.Scene scene, android.support.transition.Transition transition) {
        mSceneTransitions.put(scene, transition);
    }

    /**
     * Sets a specific transition to occur when the given pair of scenes is
     * exited/entered.
     *
     * @param fromScene The scene being exited when the given transition will
     * be run
     * @param toScene The scene being entered when the given transition will
     * be run
     * @param transition The transition that will play when the given scene is
     * entered. A value of null will result in the default behavior of
     * using the {@link #getDefaultTransition() default transition} instead.
     */
    public void setTransition(android.support.transition.Scene fromScene, android.support.transition.Scene toScene, android.support.transition.Transition transition) {
        ArrayMap<android.support.transition.Scene, android.support.transition.Transition> sceneTransitionMap = mScenePairTransitions.get(toScene);
        if (sceneTransitionMap == null) {
            sceneTransitionMap = new ArrayMap<android.support.transition.Scene, android.support.transition.Transition>();
            mScenePairTransitions.put(toScene, sceneTransitionMap);
        }
        sceneTransitionMap.put(fromScene, transition);
    }

    /**
     * Returns the Transition for the given scene being entered. The result
     * depends not only on the given scene, but also the scene which the
     * {@link android.support.transition.Scene#getSceneRoot() sceneRoot} of the Scene is currently in.
     *
     * @param scene The scene being entered
     * @return The Transition to be used for the given scene change. If no
     * Transition was specified for this scene change, the {@link #getDefaultTransition()
     * default transition} will be used instead.
     */
    private android.support.transition.Transition getTransition(android.support.transition.Scene scene) {
        android.support.transition.Transition transition = null;
        ViewGroup sceneRoot = scene.getSceneRoot();
        if (sceneRoot != null) {
            // TODO: cached in Scene instead? long-term, cache in View itself
            android.support.transition.Scene currScene = android.support.transition.Scene.getCurrentScene(sceneRoot);
            if (currScene != null) {
                ArrayMap<android.support.transition.Scene, android.support.transition.Transition> sceneTransitionMap = mScenePairTransitions.get(scene);
                if (sceneTransitionMap != null) {
                    transition = sceneTransitionMap.get(currScene);
                    if (transition != null) {
                        return transition;
                    }
                }
            }
        }
        transition = mSceneTransitions.get(scene);
        return (transition != null) ? transition : sDefaultTransition;
    }

    /**
     * This is where all of the work of a transition/scene-change is
     * orchestrated. This method captures the start values for the given
     * transition, exits the current Scene, enters the new scene, captures
     * the end values for the transition, and finally plays the
     * resulting values-populated transition.
     *
     * @param scene The scene being entered
     * @param transition The transition to play for this scene change
     */
    private static void changeScene(android.support.transition.Scene scene, android.support.transition.Transition transition) {

        final ViewGroup sceneRoot = scene.getSceneRoot();

        android.support.transition.Transition transitionClone = transition.clone();
        transitionClone.setSceneRoot(sceneRoot);

        android.support.transition.Scene oldScene = android.support.transition.Scene.getCurrentScene(sceneRoot);
        if (oldScene != null && oldScene.isCreatedFromLayoutResource()) {
            transitionClone.setCanRemoveViews(true);
        }

        sceneChangeSetup(sceneRoot, transitionClone);

        scene.enter();

        sceneChangeRunTransition(sceneRoot, transitionClone);
    }

    private static ArrayMap<ViewGroup, ArrayList<android.support.transition.Transition>> getRunningTransitions() {
        WeakReference<ArrayMap<ViewGroup, ArrayList<android.support.transition.Transition>>> runningTransitions =
                sRunningTransitions.get();
        if (runningTransitions == null || runningTransitions.get() == null) {
            ArrayMap<ViewGroup, ArrayList<android.support.transition.Transition>> transitions =
                    new ArrayMap<ViewGroup, ArrayList<android.support.transition.Transition>>();
            runningTransitions = new WeakReference<ArrayMap<ViewGroup, ArrayList<android.support.transition.Transition>>>(
                    transitions);
            sRunningTransitions.set(runningTransitions);
        }
        return runningTransitions.get();
    }

    private static void sceneChangeRunTransition(final ViewGroup sceneRoot,
            final android.support.transition.Transition transition) {
        if (transition != null) {
            OverlayCompatibilityHelper.addViewOverlayCompat(sceneRoot);
            final ViewTreeObserver observer = sceneRoot.getViewTreeObserver();
            final ViewTreeObserver.OnPreDrawListener listener =
                    new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    sceneRoot.getViewTreeObserver().removeOnPreDrawListener(this);
                    sPendingTransitions.remove(sceneRoot);
                    // Add to running list, handle end to remove it
                    final ArrayMap<ViewGroup, ArrayList<android.support.transition.Transition>> runningTransitions =
                            getRunningTransitions();
                    ArrayList<android.support.transition.Transition> currentTransitions = runningTransitions.get(sceneRoot);
                    ArrayList<android.support.transition.Transition> previousRunningTransitions = null;
                    if (currentTransitions == null) {
                        currentTransitions = new ArrayList<android.support.transition.Transition>();
                        runningTransitions.put(sceneRoot, currentTransitions);
                    } else if (currentTransitions.size() > 0) {
                        previousRunningTransitions = new ArrayList<android.support.transition.Transition>(currentTransitions);
                    }
                    currentTransitions.add(transition);
                    transition.addListener(new android.support.transition.Transition.TransitionListenerAdapter() {
                        @Override
                        public void onTransitionEnd(android.support.transition.Transition transition) {
                            ArrayList<android.support.transition.Transition> currentTransitions =
                                    runningTransitions.get(sceneRoot);
                            currentTransitions.remove(transition);
                        }
                    });
                    transition.captureValues(sceneRoot, false);
                    if (previousRunningTransitions != null) {
                        for (android.support.transition.Transition runningTransition : previousRunningTransitions) {
                            runningTransition.resume();
                        }
                    }
                    transition.playTransition(sceneRoot);

                    return true;
                }
            };
            observer.addOnPreDrawListener(listener);
        }
    }

    private static void sceneChangeSetup(ViewGroup sceneRoot, android.support.transition.Transition transition) {

        // Capture current values
        ArrayList<android.support.transition.Transition> runningTransitions = getRunningTransitions().get(sceneRoot);

        if (runningTransitions != null && runningTransitions.size() > 0) {
            for (android.support.transition.Transition runningTransition : runningTransitions) {
                runningTransition.pause();
            }
        }

        if (transition != null) {
            transition.captureValues(sceneRoot, true);
        }

        // Notify previous scene that it is being exited
        android.support.transition.Scene previousScene = android.support.transition.Scene.getCurrentScene(sceneRoot);
        if (previousScene != null) {
            previousScene.exit();
        }
    }

    /**
     * Change to the given scene, using the
     * appropriate transition for this particular scene change
     * (as specified to the TransitionManager, or the default
     * if no such transition exists).
     *
     * @param scene The Scene to change to
     */
    public void transitionTo(android.support.transition.Scene scene) {
        // Auto transition if there is no transition declared for the Scene, but there is
        // a root or parent view
        changeScene(scene, getTransition(scene));

    }

    /**
     * Convenience method to simply change to the given scene using
     * the default transition for TransitionManager.
     *
     * @param scene The Scene to change to
     */
    public static void go(android.support.transition.Scene scene) {
        changeScene(scene, sDefaultTransition);
    }

    /**
     * Convenience method to simply change to the given scene using
     * the given transition.
     *
     * <p>Passing in <code>null</code> for the transition parameter will
     * result in the scene changing without any transition running, and is
     * equivalent to calling {@link android.support.transition.Scene#exit()} on the scene root's
     * current scene, followed by {@link android.support.transition.Scene#enter()} on the scene
     * specified by the <code>scene</code> parameter.</p>
     *
     * @param scene The Scene to change to
     * @param transition The transition to use for this scene change. A
     * value of null causes the scene change to happen with no transition.
     */
    public static void go(android.support.transition.Scene scene, android.support.transition.Transition transition) {
        changeScene(scene, transition);
    }

    /**
     * Convenience method to animate, using the default transition,
     * to a new scene defined by all changes within the given scene root between
     * calling this method and the next rendering frame.
     * Equivalent to calling {@link #beginDelayedTransition(android.view.ViewGroup, android.support.transition.Transition)}
     * with a value of <code>null</code> for the <code>transition</code> parameter.
     *
     * @param sceneRoot The root of the View hierarchy to run the transition on.
     */
    public static void beginDelayedTransition(final ViewGroup sceneRoot) {
        beginDelayedTransition(sceneRoot, null);
    }

    /**
     * Convenience method to animate to a new scene defined by all changes within
     * the given scene root between calling this method and the next rendering frame.
     * Calling this method causes TransitionManager to capture current values in the
     * scene root and then post a request to run a transition on the next frame.
     * At that time, the new values in the scene root will be captured and changes
     * will be animated. There is no need to create a Scene; it is implied by
     * changes which take place between calling this method and the next frame when
     * the transition begins.
     *
     * <p>Calling this method several times before the next frame (for example, if
     * unrelated code also wants to make dynamic changes and run a transition on
     * the same scene root), only the first call will trigger capturing values
     * and exiting the current scene. Subsequent calls to the method with the
     * same scene root during the same frame will be ignored.</p>
     *
     * <p>Passing in <code>null</code> for the transition parameter will
     * cause the TransitionManager to use its default transition.</p>
     *
     * @param sceneRoot The root of the View hierarchy to run the transition on.
     * @param transition The transition to use for this change. A
     * value of null causes the TransitionManager to use the default transition.
     */
    public static void beginDelayedTransition(final ViewGroup sceneRoot, android.support.transition.Transition transition) {
        if (!sPendingTransitions.contains(sceneRoot)
                //TODO
                //& sceneRoot.isLaidOut()
                ) {
            if (android.support.transition.Transition.DBG) {
                Log.d(LOG_TAG, "beginDelayedTransition: root, transition = " +
                        sceneRoot + ", " + transition);
            }
            sPendingTransitions.add(sceneRoot);
            if (transition == null) {
                transition = sDefaultTransition;
            }
            final android.support.transition.Transition transitionClone = transition.clone();
            sceneChangeSetup(sceneRoot, transitionClone);
            Scene.setCurrentScene(sceneRoot, null);
            sceneChangeRunTransition(sceneRoot, transitionClone);
        }
    }
}
