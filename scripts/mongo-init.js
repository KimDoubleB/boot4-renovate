// MongoDB 초기화 스크립트
// 사용법: mongosh petclinic --file mongo-init.js
// Docker Compose에서는 /docker-entrypoint-initdb.d/ 에 배치

db = db.getSiblingDB('petclinic');

// 기존 데이터 확인
if (db.owners.countDocuments() > 0) {
    print("데이터가 이미 존재합니다. 초기화를 건너뜁니다.");
    quit();
}

print("샘플 데이터 초기화 시작...");

const now = new Date();

// ============================================
// Owners Collection
// ============================================
const owners = [
    {
        _id: ObjectId(),
        firstName: "김",
        lastName: "민수",
        email: "minsu.kim@example.com",
        phone: "010-1234-5678",
        address: "서울시 강남구 역삼동 123-45",
        createdAt: now,
        updatedAt: now,
        _class: "com.kirndoubleb.labs.domain.model.Owner"
    },
    {
        _id: ObjectId(),
        firstName: "이",
        lastName: "지은",
        email: "jieun.lee@example.com",
        phone: "010-2345-6789",
        address: "서울시 서초구 방배동 67-89",
        createdAt: now,
        updatedAt: now,
        _class: "com.kirndoubleb.labs.domain.model.Owner"
    },
    {
        _id: ObjectId(),
        firstName: "박",
        lastName: "현우",
        email: "hyunwoo.park@example.com",
        phone: "010-3456-7890",
        address: "경기도 성남시 분당구 정자동 11-22",
        createdAt: now,
        updatedAt: now,
        _class: "com.kirndoubleb.labs.domain.model.Owner"
    },
    {
        _id: ObjectId(),
        firstName: "최",
        lastName: "수진",
        email: "sujin.choi@example.com",
        phone: "010-4567-8901",
        address: "서울시 마포구 합정동 33-44",
        createdAt: now,
        updatedAt: now,
        _class: "com.kirndoubleb.labs.domain.model.Owner"
    },
    {
        _id: ObjectId(),
        firstName: "정",
        lastName: "태영",
        email: "taeyoung.jung@example.com",
        phone: "010-5678-9012",
        address: "인천시 연수구 송도동 55-66",
        createdAt: now,
        updatedAt: now,
        _class: "com.kirndoubleb.labs.domain.model.Owner"
    }
];

db.owners.insertMany(owners);
print(`✓ ${owners.length}명의 Owner 생성 완료`);

// ============================================
// Pets Collection
// ============================================
const pets = [
    // 김민수's pets
    {
        _id: ObjectId(),
        name: "초코",
        species: "DOG",
        breed: "말티즈",
        birthDate: ISODate("2021-03-15"),
        ownerId: owners[0]._id.toString(),
        createdAt: now,
        updatedAt: now,
        _class: "com.kirndoubleb.labs.domain.model.Pet"
    },
    {
        _id: ObjectId(),
        name: "나비",
        species: "CAT",
        breed: "코리안숏헤어",
        birthDate: ISODate("2020-07-22"),
        ownerId: owners[0]._id.toString(),
        createdAt: now,
        updatedAt: now,
        _class: "com.kirndoubleb.labs.domain.model.Pet"
    },
    // 이지은's pets
    {
        _id: ObjectId(),
        name: "뽀삐",
        species: "DOG",
        breed: "포메라니안",
        birthDate: ISODate("2022-01-10"),
        ownerId: owners[1]._id.toString(),
        createdAt: now,
        updatedAt: now,
        _class: "com.kirndoubleb.labs.domain.model.Pet"
    },
    // 박현우's pets
    {
        _id: ObjectId(),
        name: "레오",
        species: "CAT",
        breed: "러시안블루",
        birthDate: ISODate("2019-11-05"),
        ownerId: owners[2]._id.toString(),
        createdAt: now,
        updatedAt: now,
        _class: "com.kirndoubleb.labs.domain.model.Pet"
    },
    {
        _id: ObjectId(),
        name: "피피",
        species: "BIRD",
        breed: "사랑앵무",
        birthDate: ISODate("2023-02-28"),
        ownerId: owners[2]._id.toString(),
        createdAt: now,
        updatedAt: now,
        _class: "com.kirndoubleb.labs.domain.model.Pet"
    },
    // 최수진's pets
    {
        _id: ObjectId(),
        name: "몽이",
        species: "DOG",
        breed: "시바이누",
        birthDate: ISODate("2020-09-18"),
        ownerId: owners[3]._id.toString(),
        createdAt: now,
        updatedAt: now,
        _class: "com.kirndoubleb.labs.domain.model.Pet"
    },
    {
        _id: ObjectId(),
        name: "니모",
        species: "FISH",
        breed: "클라운피쉬",
        birthDate: ISODate("2024-04-01"),
        ownerId: owners[3]._id.toString(),
        createdAt: now,
        updatedAt: now,
        _class: "com.kirndoubleb.labs.domain.model.Pet"
    },
    // 정태영's pets
    {
        _id: ObjectId(),
        name: "토리",
        species: "RABBIT",
        breed: "네덜란드드워프",
        birthDate: ISODate("2022-06-12"),
        ownerId: owners[4]._id.toString(),
        createdAt: now,
        updatedAt: now,
        _class: "com.kirndoubleb.labs.domain.model.Pet"
    },
    {
        _id: ObjectId(),
        name: "맥스",
        species: "DOG",
        breed: "골든리트리버",
        birthDate: ISODate("2018-12-25"),
        ownerId: owners[4]._id.toString(),
        createdAt: now,
        updatedAt: now,
        _class: "com.kirndoubleb.labs.domain.model.Pet"
    }
];

db.pets.insertMany(pets);
print(`✓ ${pets.length}마리의 Pet 생성 완료`);

// ============================================
// Vets Collection
// ============================================
const vets = [
    {
        _id: ObjectId(),
        firstName: "한",
        lastName: "의진",
        licenseNumber: "VET-2015-001",
        specialties: ["내과", "피부과"],
        available: true,
        createdAt: now,
        updatedAt: now,
        _class: "com.kirndoubleb.labs.domain.model.Vet"
    },
    {
        _id: ObjectId(),
        firstName: "강",
        lastName: "서준",
        licenseNumber: "VET-2018-042",
        specialties: ["외과", "정형외과"],
        available: true,
        createdAt: now,
        updatedAt: now,
        _class: "com.kirndoubleb.labs.domain.model.Vet"
    },
    {
        _id: ObjectId(),
        firstName: "윤",
        lastName: "미래",
        licenseNumber: "VET-2020-088",
        specialties: ["치과", "안과"],
        available: true,
        createdAt: now,
        updatedAt: now,
        _class: "com.kirndoubleb.labs.domain.model.Vet"
    },
    {
        _id: ObjectId(),
        firstName: "임",
        lastName: "동현",
        licenseNumber: "VET-2012-015",
        specialties: ["심장내과", "응급의학"],
        available: false,
        createdAt: now,
        updatedAt: now,
        _class: "com.kirndoubleb.labs.domain.model.Vet"
    },
    {
        _id: ObjectId(),
        firstName: "송",
        lastName: "하늘",
        licenseNumber: "VET-2022-103",
        specialties: ["예방의학", "영양학"],
        available: true,
        createdAt: now,
        updatedAt: now,
        _class: "com.kirndoubleb.labs.domain.model.Vet"
    }
];

db.vets.insertMany(vets);
print(`✓ ${vets.length}명의 Vet 생성 완료`);

// ============================================
// Visits Collection
// ============================================
const visits = [
    {
        _id: ObjectId(),
        petId: pets[0]._id.toString(),  // 초코
        vetId: vets[0]._id.toString(),  // 한의진
        visitDate: new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000),
        diagnosis: "경미한 피부 알레르기",
        treatment: "항히스타민제 처방, 저알레르기 샴푸 권장",
        notes: "2주 후 경과 확인 필요",
        cost: NumberDecimal("85000"),
        paymentStatus: "COMPLETED",
        createdAt: now,
        updatedAt: now,
        _class: "com.kirndoubleb.labs.domain.model.Visit"
    },
    {
        _id: ObjectId(),
        petId: pets[1]._id.toString(),  // 나비
        vetId: vets[2]._id.toString(),  // 윤미래
        visitDate: new Date(now.getTime() - 45 * 24 * 60 * 60 * 1000),
        diagnosis: "치석 축적",
        treatment: "스케일링 시술",
        notes: "연 1회 정기 치과 검진 권장",
        cost: NumberDecimal("150000"),
        paymentStatus: "COMPLETED",
        createdAt: now,
        updatedAt: now,
        _class: "com.kirndoubleb.labs.domain.model.Visit"
    },
    {
        _id: ObjectId(),
        petId: pets[2]._id.toString(),  // 뽀삐
        vetId: vets[4]._id.toString(),  // 송하늘
        visitDate: new Date(now.getTime() - 14 * 24 * 60 * 60 * 1000),
        diagnosis: "정기 건강검진",
        treatment: "예방접종 (종합백신)",
        notes: "전반적으로 건강 양호",
        cost: NumberDecimal("65000"),
        paymentStatus: "COMPLETED",
        createdAt: now,
        updatedAt: now,
        _class: "com.kirndoubleb.labs.domain.model.Visit"
    },
    {
        _id: ObjectId(),
        petId: pets[3]._id.toString(),  // 레오
        vetId: vets[0]._id.toString(),  // 한의진
        visitDate: new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000),
        diagnosis: "구토 증상",
        treatment: "수액 처치, 위장약 처방",
        notes: "3일 금식 후 연식 급여",
        cost: NumberDecimal("120000"),
        paymentStatus: "PENDING",
        createdAt: now,
        updatedAt: now,
        _class: "com.kirndoubleb.labs.domain.model.Visit"
    },
    {
        _id: ObjectId(),
        petId: pets[5]._id.toString(),  // 몽이
        vetId: vets[1]._id.toString(),  // 강서준
        visitDate: new Date(now.getTime() - 60 * 24 * 60 * 60 * 1000),
        diagnosis: "슬개골 탈구 의심",
        treatment: "엑스레이 촬영, 보존적 치료",
        notes: "과격한 운동 자제, 3개월 후 재검",
        cost: NumberDecimal("250000"),
        paymentStatus: "COMPLETED",
        createdAt: now,
        updatedAt: now,
        _class: "com.kirndoubleb.labs.domain.model.Visit"
    },
    {
        _id: ObjectId(),
        petId: pets[8]._id.toString(),  // 맥스
        vetId: vets[3]._id.toString(),  // 임동현
        visitDate: new Date(now.getTime() - 90 * 24 * 60 * 60 * 1000),
        diagnosis: "심잡음 발견",
        treatment: "심전도 검사, 심장 초음파",
        notes: "6개월마다 정기 심장 검진 필요",
        cost: NumberDecimal("350000"),
        paymentStatus: "COMPLETED",
        createdAt: now,
        updatedAt: now,
        _class: "com.kirndoubleb.labs.domain.model.Visit"
    }
];

db.visits.insertMany(visits);
print(`✓ ${visits.length}건의 Visit 생성 완료`);

// ============================================
// Appointments Collection
// ============================================
const appointments = [
    {
        _id: ObjectId(),
        petId: pets[0]._id.toString(),  // 초코
        vetId: vets[0]._id.toString(),  // 한의진
        scheduledAt: new Date(now.getTime() + 3 * 24 * 60 * 60 * 1000),
        status: "CONFIRMED",
        reason: "피부 알레르기 경과 확인",
        reminderSent: true,
        createdAt: now,
        updatedAt: now,
        _class: "com.kirndoubleb.labs.domain.model.Appointment"
    },
    {
        _id: ObjectId(),
        petId: pets[3]._id.toString(),  // 레오
        vetId: vets[0]._id.toString(),  // 한의진
        scheduledAt: new Date(now.getTime() + 5 * 24 * 60 * 60 * 1000),
        status: "SCHEDULED",
        reason: "구토 증상 재진",
        reminderSent: false,
        createdAt: now,
        updatedAt: now,
        _class: "com.kirndoubleb.labs.domain.model.Appointment"
    },
    {
        _id: ObjectId(),
        petId: pets[4]._id.toString(),  // 피피
        vetId: vets[2]._id.toString(),  // 윤미래
        scheduledAt: new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000),
        status: "SCHEDULED",
        reason: "정기 건강검진",
        reminderSent: false,
        createdAt: now,
        updatedAt: now,
        _class: "com.kirndoubleb.labs.domain.model.Appointment"
    },
    {
        _id: ObjectId(),
        petId: pets[5]._id.toString(),  // 몽이
        vetId: vets[1]._id.toString(),  // 강서준
        scheduledAt: new Date(now.getTime() + 14 * 24 * 60 * 60 * 1000),
        status: "SCHEDULED",
        reason: "슬개골 재검진",
        reminderSent: false,
        createdAt: now,
        updatedAt: now,
        _class: "com.kirndoubleb.labs.domain.model.Appointment"
    },
    {
        _id: ObjectId(),
        petId: pets[7]._id.toString(),  // 토리
        vetId: vets[4]._id.toString(),  // 송하늘
        scheduledAt: new Date(now.getTime() + 10 * 24 * 60 * 60 * 1000),
        status: "CONFIRMED",
        reason: "예방접종",
        reminderSent: true,
        createdAt: now,
        updatedAt: now,
        _class: "com.kirndoubleb.labs.domain.model.Appointment"
    },
    {
        _id: ObjectId(),
        petId: pets[8]._id.toString(),  // 맥스
        vetId: vets[3]._id.toString(),  // 임동현
        scheduledAt: new Date(now.getTime() + 21 * 24 * 60 * 60 * 1000),
        status: "SCHEDULED",
        reason: "심장 정기 검진",
        reminderSent: false,
        createdAt: now,
        updatedAt: now,
        _class: "com.kirndoubleb.labs.domain.model.Appointment"
    },
    // Cancelled appointment
    {
        _id: ObjectId(),
        petId: pets[2]._id.toString(),  // 뽀삐
        vetId: vets[0]._id.toString(),  // 한의진
        scheduledAt: new Date(now.getTime() + 2 * 24 * 60 * 60 * 1000),
        status: "CANCELLED",
        reason: "피부 검진",
        reminderSent: true,
        createdAt: now,
        updatedAt: now,
        _class: "com.kirndoubleb.labs.domain.model.Appointment"
    },
    // Completed appointment
    {
        _id: ObjectId(),
        petId: pets[1]._id.toString(),  // 나비
        vetId: vets[2]._id.toString(),  // 윤미래
        scheduledAt: new Date(now.getTime() - 2 * 24 * 60 * 60 * 1000),
        status: "COMPLETED",
        reason: "치과 경과 확인",
        reminderSent: true,
        createdAt: now,
        updatedAt: now,
        _class: "com.kirndoubleb.labs.domain.model.Appointment"
    }
];

db.appointments.insertMany(appointments);
print(`✓ ${appointments.length}건의 Appointment 생성 완료`);

// ============================================
// Create Indexes
// ============================================
print("\n인덱스 생성 중...");

// Owners indexes
db.owners.createIndex({ email: 1 }, { unique: true });
db.owners.createIndex({ lastName: 1 });

// Pets indexes
db.pets.createIndex({ ownerId: 1 });
db.pets.createIndex({ species: 1 });
db.pets.createIndex({ name: 1 });

// Vets indexes
db.vets.createIndex({ licenseNumber: 1 }, { unique: true });
db.vets.createIndex({ available: 1 });
db.vets.createIndex({ specialties: 1 });

// Visits indexes
db.visits.createIndex({ petId: 1 });
db.visits.createIndex({ vetId: 1 });
db.visits.createIndex({ paymentStatus: 1 });
db.visits.createIndex({ visitDate: -1 });

// Appointments indexes
db.appointments.createIndex({ petId: 1 });
db.appointments.createIndex({ vetId: 1 });
db.appointments.createIndex({ status: 1 });
db.appointments.createIndex({ scheduledAt: 1 });
db.appointments.createIndex({ reminderSent: 1, scheduledAt: 1 });

print("✓ 인덱스 생성 완료");

// ============================================
// Summary
// ============================================
print("\n========================================");
print("샘플 데이터 초기화 완료!");
print("========================================");
print(`Owners: ${db.owners.countDocuments()}명`);
print(`Pets: ${db.pets.countDocuments()}마리`);
print(`Vets: ${db.vets.countDocuments()}명`);
print(`Visits: ${db.visits.countDocuments()}건`);
print(`Appointments: ${db.appointments.countDocuments()}건`);
print("========================================");
