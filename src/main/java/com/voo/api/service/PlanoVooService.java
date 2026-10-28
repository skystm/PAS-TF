package com.voo.api.service;

import com.voo.api.domain.model.*;
import com.voo.api.domain.repository.*;
import com.voo.api.dto.request.AdicionarTrechoRequest;
import com.voo.api.dto.request.CriarPlanoRequest;
import com.voo.api.exception.RecursoNaoEncontradoException;
import com.voo.api.exception.ValidacaoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlanoVooService {

    private final PlanoVooRepository planoVooRepository;
    private final PilotoRepository pilotoRepository;
    private final AeronaveRepository aeronaveRepository;
    private final AeroportoRepository aeroportoRepository;
    private final AeroviaRepository aeroviaRepository;
    private final ReservaAeroviaRepository reservaAeroviaRepository;
    private final ReservaVagaRepository reservaVagaRepository;
    private final DisponibilidadeService disponibilidadeService;

    public PlanoVooService(PlanoVooRepository planoVooRepository,
                           PilotoRepository pilotoRepository,
                           AeronaveRepository aeronaveRepository,
                           AeroportoRepository aeroportoRepository,
                           AeroviaRepository aeroviaRepository,
                           ReservaAeroviaRepository reservaAeroviaRepository,
                           ReservaVagaRepository reservaVagaRepository,
                           DisponibilidadeService disponibilidadeService) {
        this.planoVooRepository = planoVooRepository;
        this.pilotoRepository = pilotoRepository;
        this.aeronaveRepository = aeronaveRepository;
        this.aeroportoRepository = aeroportoRepository;
        this.aeroviaRepository = aeroviaRepository;
        this.reservaAeroviaRepository = reservaAeroviaRepository;
        this.reservaVagaRepository = reservaVagaRepository;
        this.disponibilidadeService = disponibilidadeService;
    }

    @Transactional
    public PlanoVoo criar(CriarPlanoRequest req) {
        Piloto piloto = pilotoRepository.findById(req.getPilotoId())
            .orElseThrow(() -> new RecursoNaoEncontradoException("Piloto não encontrado"));
        Aeronave aeronave = aeronaveRepository.findById(req.getAeronavePrefixo())
            .orElseThrow(() -> new RecursoNaoEncontradoException("Aeronave não encontrada"));
        Aeroporto origem = aeroportoRepository.findById(req.getOrigemCodigo())
            .orElseThrow(() -> new RecursoNaoEncontradoException("Aeroporto origem não encontrado"));
        Aeroporto destino = aeroportoRepository.findById(req.getDestinoCodigo())
            .orElseThrow(() -> new RecursoNaoEncontradoException("Aeroporto destino não encontrado"));

        PlanoVoo plano = new PlanoVoo();
        plano.setPiloto(piloto);
        plano.setAeronave(aeronave);
        plano.setOrigem(origem);
        plano.setDestino(destino);
        plano.setDataHoraPartida(req.getDataHoraPartida());
        plano.setSituacao(SituacaoPlano.EM_EDICAO);
        return planoVooRepository.save(plano);
    }

    public PlanoVoo buscar(Long id) {
        return planoVooRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Plano de voo não encontrado: " + id));
    }

    public List<PlanoVoo> listarPorSituacao(SituacaoPlano situacao) {
        if (situacao == null) return planoVooRepository.findAll();
        return planoVooRepository.findBySituacao(situacao);
    }

    @Transactional
    public PlanoVoo adicionarTrecho(Long planoId, AdicionarTrechoRequest req) {
        PlanoVoo plano = buscar(planoId);
        if (!plano.podeSerModificado()) {
            throw new ValidacaoException("Plano não está em edição");
        }

        Aerovia aerovia = aeroviaRepository.findById(req.getAeroviaId())
            .orElseThrow(() -> new RecursoNaoEncontradoException("Aerovia não encontrada: " + req.getAeroviaId()));

        List<TrechoVoo> trechos = plano.getTrechos();

        // Validate sequence
        if (trechos.isEmpty()) {
            if (!aerovia.getOrigem().getCodigo().equals(plano.getOrigem().getCodigo())) {
                throw new ValidacaoException("Origem da primeira aerovia deve ser igual à origem do plano");
            }
        } else {
            TrechoVoo ultimo = trechos.get(trechos.size() - 1);
            if (!aerovia.getOrigem().getCodigo().equals(ultimo.getAerovia().getDestino().getCodigo())) {
                throw new ValidacaoException("Origem da aerovia deve ser o destino da aerovia anterior");
            }
        }

        // Validate altitude range
        if (req.getAltitude() < 25000 || req.getAltitude() > 35000 || req.getAltitude() % 1000 != 0) {
            throw new ValidacaoException("Altitude inválida: deve ser entre 25000 e 35000 pés em incrementos de 1000");
        }

        // Calculate arrival time
        double duracaoHoras = aerovia.getDistancia() / plano.getAeronave().getVelocidadeCruzeiro();
        LocalDateTime horaEntrada = req.getHoraEntrada();
        LocalDateTime horaSaida = horaEntrada.plusMinutes((long)(duracaoHoras * 60));

        TrechoVoo trecho = new TrechoVoo();
        trecho.setPlanoVoo(plano);
        trecho.setOrdem(trechos.size());
        trecho.setAerovia(aerovia);
        trecho.setAltitude(req.getAltitude());
        trecho.setHoraEntrada(horaEntrada);
        trecho.setHoraSaida(horaSaida);
        trecho.setTipoPassagem(req.getTipoPassagem());
        trecho.setNumeroVaga(req.getNumeroVaga());
        trecho.setTempoPermanencia(req.getTempoPermanencia());

        trechos.add(trecho);
        return planoVooRepository.save(plano);
    }

    @Transactional
    public PlanoVoo removerTrechos(Long planoId) {
        PlanoVoo plano = buscar(planoId);
        if (!plano.podeSerModificado()) {
            throw new ValidacaoException("Plano não está em edição");
        }
        plano.getTrechos().clear();
        return planoVooRepository.save(plano);
    }

    public List<String> validar(Long planoId) {
        PlanoVoo plano = buscar(planoId);
        return executarValidacoes(plano);
    }

    private List<String> executarValidacoes(PlanoVoo plano) {
        List<String> erros = new ArrayList<>();
        List<TrechoVoo> trechos = plano.getTrechos();

        if (trechos.isEmpty()) {
            erros.add("O plano deve ter pelo menos um trecho");
            return erros;
        }

        TrechoVoo primeiro = trechos.get(0);
        TrechoVoo ultimo = trechos.get(trechos.size() - 1);

        // Origin consistency
        if (!primeiro.getAerovia().getOrigem().getCodigo().equals(plano.getOrigem().getCodigo())) {
            erros.add("Origem do plano não corresponde à origem da primeira aerovia");
        }

        // Destination consistency
        if (!ultimo.getAerovia().getDestino().getCodigo().equals(plano.getDestino().getCodigo())) {
            erros.add("Destino do plano não corresponde ao destino da última aerovia");
        }

        // First trecho entry time must match departure
        if (!primeiro.getHoraEntrada().equals(plano.getDataHoraPartida())) {
            erros.add("Horário de entrada na primeira aerovia deve ser igual à partida do plano");
        }

        // Airway continuity and timing
        double distanciaSemReabastecimento = 0;
        for (int i = 0; i < trechos.size(); i++) {
            TrechoVoo trecho = trechos.get(i);
            Aeronave aeronave = plano.getAeronave();

            // Validate calculated arrival
            double duracaoHoras = trecho.getAerovia().getDistancia() / aeronave.getVelocidadeCruzeiro();
            LocalDateTime chegadaEsperada = trecho.getHoraEntrada().plusMinutes((long)(duracaoHoras * 60));
            if (!trecho.getHoraSaida().equals(chegadaEsperada)) {
                erros.add("Trecho " + (i+1) + ": horário de chegada calculado incorretamente");
            }

            // Altitude restriction
            if (!aeronave.altitudePermitida(trecho.getAltitude())) {
                erros.add("Trecho " + (i+1) + ": altitude " + trecho.getAltitude() + " não permitida para esta aeronave");
            }

            // Cargo time restriction
            if (!aeronave.horarioPermitido(trecho.getHoraEntrada(), trecho.getHoraSaida())) {
                erros.add("Trecho " + (i+1) + ": horário não permitido para aeronave de carga");
            }

            // Autonomy
            distanciaSemReabastecimento += trecho.getAerovia().getDistancia();
            if (distanciaSemReabastecimento > aeronave.autonomiaEfetiva()) {
                erros.add("Trecho " + (i+1) + ": autonomia insuficiente (distância acumulada: "
                    + distanciaSemReabastecimento + " km, máximo: " + aeronave.autonomiaEfetiva() + " km)");
            }
            if (trecho.getTipoPassagem() == TipoPassagem.POUSO) {
                distanciaSemReabastecimento = 0; // reabastecimento
            }

            // Continuity between segments
            if (i < trechos.size() - 1) {
                TrechoVoo proximo = trechos.get(i + 1);
                if (!trecho.getAerovia().getDestino().getCodigo()
                        .equals(proximo.getAerovia().getOrigem().getCodigo())) {
                    erros.add("Trecho " + (i+1) + ": destino não corresponde à origem do próximo trecho");
                }

                LocalDateTime entradaEsperadaProximo;
                if (trecho.getTipoPassagem() == TipoPassagem.POUSO && trecho.getTempoPermanencia() != null) {
                    entradaEsperadaProximo = trecho.getHoraSaida().plusMinutes(trecho.getTempoPermanencia());
                } else {
                    entradaEsperadaProximo = trecho.getHoraSaida();
                }
                if (!proximo.getHoraEntrada().equals(entradaEsperadaProximo)) {
                    erros.add("Trecho " + (i+2) + ": horário de entrada não corresponde ao esperado após " +
                        (trecho.getTipoPassagem() == TipoPassagem.POUSO ? "pouso" : "sobrevoo"));
                }
            }

            // Pouso validation
            if (trecho.getTipoPassagem() == TipoPassagem.POUSO) {
                if (trecho.getNumeroVaga() == null) {
                    erros.add("Trecho " + (i+1) + ": pouso requer número de vaga");
                }
                if (trecho.getTempoPermanencia() == null) {
                    erros.add("Trecho " + (i+1) + ": pouso requer tempo de permanência");
                }
            }
        }

        // Final destination must be POUSO
        if (ultimo.getTipoPassagem() != TipoPassagem.POUSO) {
            erros.add("Destino final deve ter pouso obrigatório");
        }

        // Same day constraint: arrival at final destination must be same date as departure
        LocalDateTime chegadaFinal = ultimo.getHoraSaida();
        if (!chegadaFinal.toLocalDate().equals(plano.getDataHoraPartida().toLocalDate())) {
            erros.add("Chegada ao destino final deve ocorrer no mesmo dia da partida");
        }

        return erros;
    }

    @Transactional
    public PlanoVoo aprovar(Long planoId) {
        PlanoVoo plano = buscar(planoId);
        if (!plano.podeSerAprovado()) {
            throw new ValidacaoException("Plano não está em edição");
        }

        List<String> erros = executarValidacoes(plano);
        if (!erros.isEmpty()) {
            throw new ValidacaoException("Plano inválido: " + String.join("; ", erros));
        }

        // Check and reserve all resources atomically
        List<ReservaAerovia> novasReservasAerovia = new ArrayList<>();
        List<ReservaVaga> novasReservasVaga = new ArrayList<>();

        for (TrechoVoo trecho : plano.getTrechos()) {
            // Check altitude availability
            if (!disponibilidadeService.altitudeDisponivel(
                    trecho.getAerovia().getId(), trecho.getAltitude(),
                    trecho.getHoraEntrada(), trecho.getHoraSaida(), null)) {
                throw new ValidacaoException("Altitude " + trecho.getAltitude() +
                    " na aerovia " + trecho.getAerovia().getId() + " não está disponível");
            }

            // Create airway reservations per slot
            List<LocalDateTime> slots = disponibilidadeService.calcularSlots(
                trecho.getHoraEntrada(), trecho.getHoraSaida());
            for (LocalDateTime slot : slots) {
                ReservaAerovia reserva = new ReservaAerovia();
                reserva.setAerovia(trecho.getAerovia());
                reserva.setAltitude(trecho.getAltitude());
                reserva.setSlotInicio(slot);
                reserva.setSlotFim(slot.plusHours(1));
                reserva.setPlanoVoo(plano);
                novasReservasAerovia.add(reserva);
            }

            // Check and reserve parking spot if landing
            if (trecho.getTipoPassagem() == TipoPassagem.POUSO) {
                LocalDateTime chegada = trecho.getHoraSaida();
                LocalDateTime partida = chegada.plusMinutes(trecho.getTempoPermanencia());
                Aeroporto aeroporto = trecho.getAerovia().getDestino();
                int vaga = trecho.getNumeroVaga();

                if (vaga < 1 || vaga > aeroporto.getVagasEstacionamento()) {
                    throw new ValidacaoException("Vaga " + vaga + " inválida para o aeroporto " + aeroporto.getCodigo());
                }

                if (!disponibilidadeService.vagaDisponivel(
                        aeroporto.getCodigo(), vaga, chegada, partida, null)) {
                    throw new ValidacaoException("Vaga " + vaga + " no aeroporto " +
                        aeroporto.getCodigo() + " não está disponível");
                }

                List<LocalDateTime> slotsVaga = disponibilidadeService.calcularSlots(chegada, partida);
                for (LocalDateTime slot : slotsVaga) {
                    ReservaVaga reservaVaga = new ReservaVaga();
                    reservaVaga.setAeroporto(aeroporto);
                    reservaVaga.setNumeroVaga(vaga);
                    reservaVaga.setSlotInicio(slot);
                    reservaVaga.setSlotFim(slot.plusHours(1));
                    reservaVaga.setPlanoVoo(plano);
                    novasReservasVaga.add(reservaVaga);
                }
            }
        }

        // All checks passed — commit all reservations
        reservaAeroviaRepository.saveAll(novasReservasAerovia);
        reservaVagaRepository.saveAll(novasReservasVaga);
        plano.setSituacao(SituacaoPlano.APROVADO);
        return planoVooRepository.save(plano);
    }

    @Transactional
    public PlanoVoo abandonar(Long planoId) {
        PlanoVoo plano = buscar(planoId);
        if (!plano.podeSerAbandonado()) {
            throw new ValidacaoException("Somente planos em edição podem ser abandonados");
        }
        plano.setSituacao(SituacaoPlano.ABANDONADO);
        return planoVooRepository.save(plano);
    }

    @Transactional
    public PlanoVoo cancelar(Long planoId) {
        PlanoVoo plano = buscar(planoId);
        if (!plano.podeSerCancelado(LocalDateTime.now())) {
            throw new ValidacaoException("Plano não pode ser cancelado: não está aprovado ou a partida já ocorreu");
        }

        // Release all reservations
        reservaAeroviaRepository.deleteAll(reservaAeroviaRepository.findByPlanoVooId(planoId));
        reservaVagaRepository.deleteAll(reservaVagaRepository.findByPlanoVooId(planoId));

        plano.setSituacao(SituacaoPlano.CANCELADO);
        return planoVooRepository.save(plano);
    }
}
