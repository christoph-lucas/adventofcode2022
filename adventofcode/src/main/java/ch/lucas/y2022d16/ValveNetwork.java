package ch.lucas.y2022d16;

import java.util.*;
import java.util.stream.Collectors;

public class ValveNetwork {

    private final Map<String, Valve> valves = new HashMap<>();

    private final Map<State, Integer> achievableAdditionalPressureRelease = new HashMap<>();

    private final Map<VisitState, Integer> visitedAt = new HashMap<>();

    public ValveNetwork(List<String> valveList) {
        for (String v : valveList) {
            String[] parts = v.split(";");

            String prefix = "Valve ";
            String name = parts[0].substring(prefix.length(), prefix.length() + 2);
            int rate = Integer.valueOf(parts[0].split("=")[1]);

            prefix = " tunnels lead to valves ";
            if (!parts[1].startsWith(prefix)) prefix = " tunnel leads to valve ";
            List<String> tunnels = Arrays.stream(parts[1].substring(prefix.length()).split(", ")).toList();

            Valve valve = new Valve(name, rate, tunnels);
            valves.put(name, valve);
        }
        valves.values().forEach(it -> it.resolveTunnels(valves));
    }

    public int computeMaxPressureReleaseMultiAgent(int ttl, int nAgents) {
        Valve[] agents = new Valve[nAgents];
        Arrays.fill(agents, valves.get("AA"));
        return computeMaxPressureReleaseMultiAgent(agents, 0, ttl, 0, 0);
    }

    public int computeMaxPressureReleaseMultiAgent(Valve[] agents, int curAgent, int ttl, int releasedPressure, int knownMax) {
        State state = null;
        if (curAgent == 0) {
            if (ttl <= 1) return releasedPressure;
            String positions = Arrays.stream(agents).map(Valve::name).sorted().collect(Collectors.joining(""));
            String openValves = getOpenValves();
            state = new State(openValves, ttl, positions);
            if (achievableAdditionalPressureRelease.containsKey(state)) {
                return releasedPressure + achievableAdditionalPressureRelease.get(state);
            }

            int currentFlow = getOpenValvesFlowRate();
            // if I got the same position with the same flow but more ttl -> that was probably a better state -> prune
            VisitState visitStateOnPosAndFlow = new VisitState(positions, currentFlow);
            if (visitedAt.containsKey(visitStateOnPosAndFlow)) {
                if (visitedAt.get(visitStateOnPosAndFlow) >= ttl) return knownMax;
            }
            visitedAt.put(visitStateOnPosAndFlow, ttl);

            int bcpr = getBestConceivablePressureRelease(ttl + 1, agents.length);
            if (releasedPressure + bcpr < knownMax) return releasedPressure;

            ttl--;
        }

        Valve cur = agents[curAgent];
        int max = releasedPressure;
        int nextAgent = (curAgent + 1) % agents.length;

        if (cur.isClosed() && cur.rate() > 0) {
            cur.openValve();
            int t = computeMaxPressureReleaseMultiAgent(agents, nextAgent, ttl, releasedPressure + (ttl * cur.rate()), max);
            max = Math.max(max, t);
            cur.closeValve();
        }

        for (Valve n : cur.tunnels()) {
            agents[curAgent] = n;
            int t = computeMaxPressureReleaseMultiAgent(agents, nextAgent, ttl, releasedPressure, max);
            max = Math.max(max, t);
        }
        agents[curAgent] = cur;

        if (curAgent == 0) achievableAdditionalPressureRelease.put(state, max - releasedPressure);
        return max;
    }

    private String getOpenValves() {
        return valves.values().stream()
                .filter(Valve::isOpen).map(Valve::name)
                .sorted().collect(Collectors.joining(""));
    }

    private int getOpenValvesFlowRate() {
        return valves.values().stream()
                .filter(Valve::isOpen).map(Valve::rate)
                .reduce(Integer::sum).orElse(0);
    }

    private int getBestConceivablePressureRelease(int ttl, int nAgents) {
        List<Valve> closedValvesDesc = valves.values().stream()
                .filter(Valve::isClosed).filter(it -> it.rate() > 0)
                .sorted(Comparator.comparing(Valve::rate).reversed()).toList();

        int result = 0;
        Iterator<Valve> valveIterator = closedValvesDesc.iterator();
        while (ttl >= 2) {
            if (!valveIterator.hasNext()) break;
            ttl -= 2; // walk there and open the valves
            for (int i = 0; i < nAgents; i++) {
                if (valveIterator.hasNext()) {
                    result += ttl * valveIterator.next().rate();
                } else {
                    break;
                }
            }
        }
        return result;
    }

    private static final class Valve {

        private final String name;
        private final int rate;
        private final List<String> tunnels;

        private List<Valve> tunnelsToValves;
        private boolean open = false;

        public Valve(String name, int rate, List<String> tunnels) {
            this.name = name;
            this.rate = rate;
            this.tunnels = tunnels;
        }

        public void resolveTunnels(Map<String, Valve> valves) {
            tunnelsToValves = tunnels.stream()
                    .map(valves::get)
                    .sorted(Comparator.comparing(Valve::rate).reversed())
                    .toList();
        }

        public int rate() {
            return rate;
        }

        public boolean isOpen() {
            return open;
        }

        public boolean isClosed() {
            return !open;
        }

        public void openValve() {
            open = true;
        }

        public void closeValve() {
            open = false;
        }

        public List<Valve> tunnels() {
            return tunnelsToValves;
        }

        public String name() {
            return name;
        }
    }

    private record State(String openValves, int ttl, String cur) {
    }

    private record VisitState(String positions, Integer flow) {
    }

}
