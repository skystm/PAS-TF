package com.voo.api.service;

import com.voo.api.domain.model.ReservaAerovia;
import com.voo.api.domain.model.ReservaVaga;
import com.voo.api.domain.repository.ReservaAeroviaRepository;
import com.voo.api.domain.repository.ReservaVagaRepository;
import com.voo.api.domain.repository.AeroportoRepository;
import com.voo.api.exception.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class DisponibilidadeService {

    private static final int ALTITUDE_MIN = 25000;
    private static final int ALTITUDE_MAX = 35000;
    private static final int ALTITUDE_STEP = 1000;

    private final ReservaAeroviaRepository reservaAeroviaRepository;
    private final ReservaVagaRepository reservaVagaRepository;
    private final AeroportoRepository aeroportoRepository;

    public DisponibilidadeService(ReservaAeroviaRepository reservaAeroviaRepository,
                                  ReservaVagaRepository reservaVagaRepository,
                                  AeroportoRepository aeroportoRepository) {
        this.reservaAeroviaRepository = reservaAeroviaRepository;
        this.reservaVagaRepository = reservaVagaRepository;
        this.aeroportoRepository = aeroportoRepository;
    }

    public List<LocalDateTime> calcularSlots(LocalDateTime inicio, LocalDateTime fim) {
        List<LocalDateTime> slots = new ArrayList<>();
        LocalDateTime slotAtual = inicio.withMinute(0).withSecond(0).withNano(0);
        LocalDateTime slotFimInclusive = fim.withMinute(0).withSecond(0).withNano(0);
        // if fim has any minutes/seconds beyond the hour, include that hour slot too
        if (fim.getMinute() > 0 || fim.getSecond() > 0 || fim.getNano() > 0) {
            slotFimInclusive = slotFimInclusive.plusHours(1);
        }
        while (!slotAtual.isAfter(slotFimInclusive.minusHours(1))) {
            slots.add(slotAtual);
            slotAtual = slotAtual.plusHours(1);
        }
        return slots;
    }

    public List<Integer> altitudesDisponiveisAerovia(String aeroviaId, LocalDateTime inicio, LocalDateTime fim) {
        List<LocalDateTime> slots = calcularSlots(inicio, fim);
        List<Integer> disponiveis = new ArrayList<>();

        for (int alt = ALTITUDE_MIN; alt <= ALTITUDE_MAX; alt += ALTITUDE_STEP) {
            final int altitude = alt;
            boolean livre = true;
            for (LocalDateTime slot : slots) {
                LocalDateTime slotFim = slot.plusHours(1);
                List<ReservaAerovia> conflitos = reservaAeroviaRepository.findConflitos(
                    aeroviaId, altitude, slot, slotFim);
                if (!conflitos.isEmpty()) {
                    livre = false;
                    break;
                }
            }
            if (livre) {
                disponiveis.add(altitude);
            }
        }
        return disponiveis;
    }

    public List<Integer> vagasDisponiveisAeroporto(String aeroportoCodigo, LocalDateTime inicio, LocalDateTime fim) {
        if (!aeroportoRepository.existsById(aeroportoCodigo)) {
            throw new RecursoNaoEncontradoException("Aeroporto não encontrado: " + aeroportoCodigo);
        }
        int totalVagas = aeroportoRepository.findById(aeroportoCodigo).get().getVagasEstacionamento();
        List<LocalDateTime> slots = calcularSlots(inicio, fim);
        List<Integer> vagasDisponiveis = new ArrayList<>();

        for (int vaga = 1; vaga <= totalVagas; vaga++) {
            final int numeroVaga = vaga;
            boolean livre = true;
            for (LocalDateTime slot : slots) {
                LocalDateTime slotFim = slot.plusHours(1);
                List<ReservaVaga> conflitos = reservaVagaRepository.findConflitos(
                    aeroportoCodigo, numeroVaga, slot, slotFim);
                if (!conflitos.isEmpty()) {
                    livre = false;
                    break;
                }
            }
            if (livre) {
                vagasDisponiveis.add(numeroVaga);
            }
        }
        return vagasDisponiveis;
    }

    public boolean altitudeDisponivel(String aeroviaId, int altitude, LocalDateTime inicio, LocalDateTime fim, Long excluirPlanoId) {
        List<LocalDateTime> slots = calcularSlots(inicio, fim);
        for (LocalDateTime slot : slots) {
            LocalDateTime slotFim = slot.plusHours(1);
            List<ReservaAerovia> conflitos = reservaAeroviaRepository.findConflitos(
                aeroviaId, altitude, slot, slotFim);
            List<ReservaAerovia> conflitosRelevantes = conflitos.stream()
                .filter(r -> excluirPlanoId == null || !r.getPlanoVoo().getId().equals(excluirPlanoId))
                .collect(Collectors.toList());
            if (!conflitosRelevantes.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public boolean vagaDisponivel(String aeroportoCodigo, int numeroVaga, LocalDateTime inicio, LocalDateTime fim, Long excluirPlanoId) {
        List<LocalDateTime> slots = calcularSlots(inicio, fim);
        for (LocalDateTime slot : slots) {
            LocalDateTime slotFim = slot.plusHours(1);
            List<ReservaVaga> conflitos = reservaVagaRepository.findConflitos(
                aeroportoCodigo, numeroVaga, slot, slotFim);
            List<ReservaVaga> conflitosRelevantes = conflitos.stream()
                .filter(r -> excluirPlanoId == null || !r.getPlanoVoo().getId().equals(excluirPlanoId))
                .collect(Collectors.toList());
            if (!conflitosRelevantes.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
